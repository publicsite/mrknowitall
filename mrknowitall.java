/*

Copyright (C) 2011 by J05HYYY

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.ArrayList;

public class mrknowitall extends HttpServlet
{

BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	void listbyfreq(PrintWriter out, ArrayList<Integer> vresponseindexs, ArrayList<Integer> vlinefreq, int linenumber, Connection conn, Statement stmt, ResultSet rs)
	{
		int biggest = 0;
		int responseindex=0;
		int t;
		for (t=0; t < vlinefreq.size(); t++)
		{
			if ( vlinefreq.get(t) >= biggest)
			{
				biggest=vlinefreq.get(t);
				responseindex=t;
			}
		} //find the biggest in vlinefreq and get the index of the biggest response (response num)

		if ( biggest != 0 )
		{
		//System.out.println("responseindex=" + responseindex);
			int crackattack = 0;
			Boolean foundresponsealready = false;
			int u;

				try
				{
					rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + linenumber + "\"");
					while (rs.next())
					{
						crackattack = rs.getInt("a" + responseindex); //get line response for biggest response
					}
				}
				catch (SQLException e )
				{
					System.out.println("Oh no!aaa");
				}

				for (u=0; u < vresponseindexs.size(); u++) if ( crackattack == vresponseindexs.get(u) )
				{
					foundresponsealready = true;
					break;
				} //check that response is not already in the queue to be outputted

				if ( foundresponsealready == false )
				{
						try
						{
							rs = stmt.executeQuery("SELECT * FROM t_responses WHERE fresponsenum = \"" + crackattack + "\"");
							while (rs.next())
							{
									if (vresponseindexs.size() == 0)
									{
										out.println("<option selected=true value=" + vresponseindexs.size() + ">" + rs.getString("fresponses") + "</option>");
									}
									else
									{
										out.println("<option value=" + vresponseindexs.size() + ">" + rs.getString("fresponses") + "</option>");
									}
								//System.out.println("Response " + vresponseindexs.size() + ": " + rs.getString("fresponses"));
							}
						}//output response to select box
						catch (SQLException e )
						{
							System.out.println("Oh no!bbb");
						}

					vresponseindexs.add(crackattack); //and add to vresponseindexs
				}

			vlinefreq.set(responseindex,0); //set line frequency to 0 so we don't use the same response twice
			listbyfreq(out, vresponseindexs, vlinefreq, linenumber, conn, stmt, rs); //repeat process until all responses are shown

		}
	}

	void processtemplinenum(ArrayList<Integer> vtemplinenum, ArrayList<Integer> vtemplinenumfreq, int templinenum)
	{
		int t;
		Boolean foundpattern2 = false;
			for (t = 0; t < vtemplinenum.size(); t++)
			{ //Check if pattern already exists in vtemplinenum
				if (templinenum == vtemplinenum.get(t))
				{
					vtemplinenumfreq.set(t,vtemplinenumfreq.get(t) + 1);
					foundpattern2 = true;
					break;
				}
			}

		if (foundpattern2 == false)
		{
			vtemplinenum.add(templinenum);										
			vtemplinenumfreq.add(1);
		}
	}

	void processlinepattern(ArrayList<Integer> vlinepatterns, ArrayList<Integer> vlinepatternfreq, int pattern)
	{
		int a;
		for (a = 0; a < vlinepatterns.size(); a++)
		{ //Check if pattern already exists in vlinepatterns
			if (pattern == vlinepatterns.get(a) )
			{
				vlinepatternfreq.set(a,vlinepatternfreq.get(a) + 1);
				return;
			}
		}

			vlinepatterns.add(pattern);
			vlinepatternfreq.add(1);
	}

	void processaddlinepattern(ArrayList<Integer> vaddlinepatterns, ArrayList<Integer> vaddlinepatternfreq, int pattern)
	{
		int a;
		for (a = 0; a < vaddlinepatterns.size(); a++)
		{ //Check if pattern already exists in vaddlinepatterns
			if (pattern == vaddlinepatterns.get(a) )
			{
				vaddlinepatternfreq.set(a,vaddlinepatternfreq.get(a) + 1);
				return;
			}
		}

			vaddlinepatterns.add(pattern);
			vaddlinepatternfreq.add(1);
	}

	void addpatternsone(Primatives primatives, ArrayList<String> vaddpatterns, ArrayList<ArrayList<Integer>> vaddlinenum, ArrayList<Integer> vaddpatternnum, ArrayList<ArrayList<Integer>> vaddlinenumtwo, ArrayList<ArrayList<Integer>> vaddlinenumfreqtwo, ArrayList<ArrayList<Integer>> vaddlinenumfreq, Connection conn, Statement stmt, ResultSet rs)
	{

		int rifour;
		for (rifour=0; rifour < vaddlinenum.size(); rifour++)
		{
			int ltwo = -1;
				try
				{
					rs = stmt.executeQuery("SELECT * FROM t_patterns WHERE BINARY(fpatterns) = '" + vaddpatterns.get(rifour) + "'");
						while (rs.next())
						{
							if (!rs.wasNull()) ltwo = rs.getInt("fpatternnum"); else ltwo = -1;
						}
				}
				catch (SQLException e )
				{
					System.out.println("Oh no! 3");
				}

				if ( ltwo != -1 )
				{ // FOUND
					vaddpatternnum.add(ltwo);
					vaddlinenumtwo.add(new ArrayList<Integer>());
					vaddlinenumtwo.get(vaddlinenumtwo.size() - 1).add(primatives.getlinenum());

					int lthree;
						for (lthree=0; lthree < vaddlinenum.get(rifour).size(); lthree++)
						{
							if (vaddlinenum.get(rifour).get(lthree) == primatives.getlinenum())
							{
								vaddlinenumfreqtwo.add(new ArrayList<Integer>());
								vaddlinenumfreqtwo.get(vaddlinenumfreqtwo.size() - 1).add(vaddlinenumfreq.get(rifour).get(lthree));
								break;
							}
						}
				}
				else
				{ // NOT FOUND

					//CREATE COLUMNS IF NOT ENOUGH ALREADY EXIST
					int numCols = 0;

						try
						{
							rs = stmt.executeQuery("SELECT * FROM t_patterns");
							ResultSetMetaData rsmd = rs.getMetaData();
							numCols = rsmd.getColumnCount();
						}
						catch (SQLException e )
						{
							System.out.println("Oh no! 5");
						}

					numCols = numCols - 2;

						for ( numCols = numCols / 2; numCols < vaddlinenum.get(rifour).size(); numCols++)
						{
							try
							{
								stmt.executeUpdate("ALTER TABLE t_patterns ADD a" + numCols + " INT");
								stmt.executeUpdate("ALTER TABLE t_patterns ADD b" + numCols + " INT");
							}
							catch (SQLException e )
							{
								System.out.println("Oh no! 5");
							}
						}
	
					//ADD TO T_PATTERNS

					String adda = "";
					String addb = "";

						int ri;
						for (ri = 0; ri < vaddlinenum.get(rifour).size(); ri++)
						{
							adda = adda + " a" + ri + "," + " b" + ri + ",";
								if ( vaddlinenum.get(rifour).get(ri) == primatives.getlinenum() )
								{
									addb = addb + " " + primatives.getnewlinenum() + ", " + vaddlinenumfreq.get(rifour).get(ri) + ",";
								}
								else addb = addb + " " + vaddlinenum.get(rifour).get(ri) + ", " + vaddlinenumfreq.get(rifour).get(ri) + ",";
						}

					adda = adda + " fpatterns ";
					addb = addb + " _binary '" + vaddpatterns.get(rifour) + "' ";
//System.out.println("wazzup4=" + line.substring(startpos,k));

						try
						{
							stmt.executeUpdate("INSERT INTO t_patterns (" + adda + ") values (" + addb + ")");
						}
						catch (SQLException e )
						{
							System.out.println("Oh no! 6");
						}
				}
		}
	}
	
	void getnumber(Primatives primatives, int max) throws IOException
	{
		System.out.println("Which response number was it?");
		String input;
		input = reader.readLine();

			try
			{
			int inpValue = Integer.parseInt(input.trim());

				if ( inpValue < 0 || inpValue > max ) getnumber(primatives, max);
				else primatives.setchosenresponse(inpValue);
			}
			catch (NumberFormatException nfe)
			{
				getnumber(primatives, max);
			}
	}

	Boolean getoperant(Primatives primatives, int max) throws IOException
	{
		System.out.println("Did your answer appear? Press return for no and space return for yes.");
		String operant;
		operant = reader.readLine();

			if ( operant.equals("") )
			{
				System.out.println("Please provide an appropriate answer.");
				primatives.setnewanswer(reader.readLine());
				return false;
			}
			else if ( operant.equals(" ") )
			{
				getnumber(primatives, max);
				return true;
			}
			else getoperant(primatives, max);
			return false;
	}

	int processresponse(String response, Connection conn, Statement stmt, ResultSet rs)
	{
		int a = -1;
			try
			{
				rs = stmt.executeQuery("SELECT * FROM t_responses WHERE BINARY(fresponses) = \"" + response + "\"");
				while (rs.next())
				{
					if (!rs.wasNull()) a = rs.getInt("fresponsenum");
				}
			}
			catch (SQLException e )
			{
				System.out.println("Oh no!1");
			}

			if ( a == -1 )
			{
				try
				{
					stmt.executeUpdate("INSERT INTO t_responses (fresponses) values (\"" + response + "\")");
					rs = stmt.executeQuery("SELECT MAX(fresponsenum) from t_responses");
					while (rs.next())
					{
						a = rs.getInt(1);
					}
				}
				catch (SQLException e )
				{
					System.out.println("Oh no!2");
				}
			}
		return a;
	}

	void addresponse(Primatives primatives, Connection conn, Statement stmt, ResultSet rs, Integer addresponseindex, Integer addresponsefreq)
	{
		try
		{
			rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + primatives.getnewlinenum() + "\"");
			ResultSetMetaData rsmd = rs.getMetaData();
			int  numCols= rsmd.getColumnCount();
			numCols= numCols- 2;
			numCols= numCols/ 2;

			Boolean addedresponse = false;
			int tone;

			while (rs.next())
			{
					for ( tone = 0; tone < numCols; tone++)
					{ // GO THROUGH VLINENUM
						int z = rs.getInt("a" + tone); //get responsenum
			
							if (rs.wasNull())
							{
								stmt.executeUpdate("UPDATE t_lines SET a" + tone + "=\"" + addresponseindex + "\" where flinenum=\"" + primatives.getnewlinenum() + "\"");//add new line
								stmt.executeUpdate("UPDATE t_lines SET b" + tone + "=\"" + addresponsefreq + "\" where flinenum=\"" + primatives.getnewlinenum() + "\"");//add new frequency																		
								addedresponse = true;
								break;
							}
					}
	
					if ( addedresponse == false )
					{ //IF THE PATTERN WASN'T ADDED BY INCREASING THE FREQUENCY OR PLACING IN AN EXISTING EMPTY COLUMN
						stmt.executeUpdate("ALTER TABLE t_lines ADD a" + numCols + " INT");
						stmt.executeUpdate("ALTER TABLE t_lines ADD b" + numCols + " INT");
						stmt.executeUpdate("UPDATE t_lines SET a" + numCols+ "=\"" + addresponseindex + "\" where flinenum=\"" + primatives.getnewlinenum() + "\"");//add new line
						stmt.executeUpdate("UPDATE t_lines SET b" + numCols+ "=\"" + addresponsefreq + "\" where flinenum=\"" + primatives.getnewlinenum() + "\"");//add new frequency
					}
				break;
			}
		}
		catch (SQLException e )
		{
			System.out.println("Oh no! 5");
		}
	}

	void addtolinefreq(ArrayList<Integer> vlinefreq, Connection conn, Statement stmt, ResultSet rs, int indextoadd)
	{
		vlinefreq.clear();
			try
			{
				rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + indextoadd + "\"");
				ResultSetMetaData rsmd = rs.getMetaData();
				int numColsthree = rsmd.getColumnCount();
				numColsthree = numColsthree - 2;
				numColsthree = numColsthree / 2;
					while (rs.next())
					{
						int crazy;
							for (crazy=0; crazy < numColsthree; crazy++)
							{
								if (!rs.wasNull())
								{
									vlinefreq.add(rs.getInt("b"+crazy));
								}
								else break;
							}
							break;
					}
			}
			catch (SQLException e )
			{
				System.out.println("Oh no!xyz");
			}
	}

	void listlinesbyfreq(PrintWriter out, ArrayList<Integer> vresponseindexs, ArrayList<Integer> vlinefreq, ArrayList<Integer> voutputlinenums, ArrayList<Integer> voutputlinenumfreq, Connection conn, Statement stmt, ResultSet rs)
	{
		int biggest = 0;
		int index = 0;
		int t;
		for (t=0; t < voutputlinenumfreq.size(); t++)
		{
			if ( voutputlinenumfreq.get(t) >= biggest)
			{
				biggest=voutputlinenumfreq.get(t);
				index=t;
			}
		}

		if ( biggest != 0 )
		{

			addtolinefreq(vlinefreq, conn, stmt, rs, voutputlinenums.get(index));
			listbyfreq(out, vresponseindexs, vlinefreq, voutputlinenums.get(index), conn, stmt, rs);
			voutputlinenumfreq.set(index,0);
			listlinesbyfreq(out, vresponseindexs, vlinefreq, voutputlinenums, voutputlinenumfreq, conn, stmt, rs);
		}
	}

	void addpatternstwo(Primatives primatives, ArrayList<Integer> vaddpatternnum, ArrayList<ArrayList<Integer>> vaddlinenumtwo, ArrayList<ArrayList<Integer>> vaddlinenumfreqtwo, Connection conn, Statement stmt, ResultSet rs)
	{
		int ritwelve;
		for (ritwelve=0; ritwelve < vaddpatternnum.size(); ritwelve++)
		{
System.out.println("gtp1");
			int t;
			Boolean addedpattern = false;
			try
			{
System.out.println("gt1");
				int rithirteen;
				for (rithirteen=0; rithirteen < vaddlinenumtwo.get(ritwelve).size(); rithirteen++)
				{
					if ( vaddlinenumtwo.get(ritwelve).get(rithirteen) == primatives.getlinenum() ) vaddlinenumtwo.get(ritwelve).set(rithirteen, primatives.getnewlinenum());

					rs = stmt.executeQuery("SELECT * FROM t_patterns WHERE fpatternnum = \"" + vaddpatternnum.get(ritwelve) + "\"");
					ResultSetMetaData rsmd = rs.getMetaData();
					int  w = rsmd.getColumnCount();
					w = w - 2;
					w = w / 2;

					while (rs.next())
					{
							for ( t = 0; t < w; t++)
							{ // GO THROUGH VLINENUM
								int z = rs.getInt("a" + t); //get linenum
			
									if ( z == vaddlinenumtwo.get(ritwelve).get(rithirteen) )
									{//if the line has been found
										int b = rs.getInt("b" + t); //get frequency
										b = b + vaddlinenumfreqtwo.get(ritwelve).get(rithirteen); //add to frequency
System.out.println("gt2");
										stmt.executeUpdate("UPDATE t_patterns SET b" + t + "=\"" + b + "\" where fpatternnum=\"" + vaddpatternnum.get(ritwelve) + "\"");//add new frequency
										addedpattern = true;
										break;
									}
			
									if (rs.wasNull())
									{
System.out.println("gt3");
										stmt.executeUpdate("UPDATE t_patterns SET a" + t + "=\"" + vaddlinenumtwo.get(ritwelve).get(rithirteen) + "\" where fpatternnum=\"" + vaddpatternnum.get(ritwelve) + "\"");//add new line
										stmt.executeUpdate("UPDATE t_patterns SET b" + t + "=\"" + vaddlinenumfreqtwo.get(ritwelve).get(rithirteen) + "\" where fpatternnum=\"" + vaddpatternnum.get(ritwelve) + "\"");//add new frequency																		
										addedpattern = true;
										break;
									}
							}
	
							if ( addedpattern == false )
							{ //IF THE PATTERN WASN'T ADDED BY INCREASING THE FREQUENCY OR PLACING IN AN EXISTING EMPTY COLUMN
System.out.println("gt4");
								stmt.executeUpdate("ALTER TABLE t_patterns ADD a" + w + " INT"); //add new columns
								stmt.executeUpdate("ALTER TABLE t_patterns ADD b" + w + " INT");
								stmt.executeUpdate("UPDATE t_patterns SET a" + w + "=\"" + vaddlinenumtwo.get(ritwelve).get(rithirteen) + "\" where fpatternnum=\"" + vaddpatternnum.get(ritwelve) + "\"");//add new line
								stmt.executeUpdate("UPDATE t_patterns SET b" + w + "=\"" + vaddlinenumfreqtwo.get(ritwelve).get(rithirteen) + "\" where fpatternnum=\"" + vaddpatternnum.get(ritwelve) + "\"");//add new frequency
System.out.println("gt5");
							}
						break;
					}
				}
System.out.println("gt6");
			}
			catch (SQLException e )
			{
			System.out.println(e.getMessage());
			}
		}
	}

	Boolean addline(Primatives primatives, ArrayList<Integer> vresponseindexs, Connection conn, Statement stmt, ResultSet rs, String line, Boolean addornot, Boolean wack)
	{

		int s = -1;
			try
			{
				rs = stmt.executeQuery("SELECT * FROM t_lines WHERE BINARY(flines) = \"" + line + "\""); // NUMBER 1
				while (rs.next())
				{
					if (!rs.wasNull()) s = rs.getInt("flinenum");
				}
				//System.out.println(s);
			}
			catch (SQLException e )
			{
				System.out.println("Oh no! 21");
			}

			if ( s != -1 )
			{ //if the line already exists
					if ( wack == true )
					{
						primatives.setnewanswer(line);
						addornot=false;
					}
				processexistingline(primatives, vresponseindexs, conn, stmt, rs, s, addornot);
				return false;
			}
			else
			{
addornot=true; //maybe can get rid of this?
				//ADD LINE

					try
					{
						stmt.executeUpdate("INSERT INTO t_lines (flines) values (\"" + line + "\")");
						rs = stmt.executeQuery("SELECT MAX( flinenum ) FROM t_lines");
						while (rs.next())
						{
							primatives.setnewlinenum(rs.getInt(1));
						}
					}
					catch (SQLException e )
					{
						System.out.println("Oh no!3");
					}
				return true;
			}
	}

	void processexistingline(Primatives primatives, ArrayList<Integer> vresponseindexs, Connection conn, Statement stmt, ResultSet rs, Integer s, Boolean addornot)
	{
		if ( addornot == true )
		{
			int newfreq = -1;
				try
				{
					rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + s + "\"");
					ResultSetMetaData rsmd = rs.getMetaData();
					int numColsfive = rsmd.getColumnCount();
					numColsfive = numColsfive - 2;
					numColsfive = numColsfive / 2;
						
					int rifourteen = -1;

					while (rs.next())
					{
							for (rifourteen=0; rifourteen < numColsfive; rifourteen++)
							{
								if ( rs.getInt("a" + rifourteen) == vresponseindexs.get(primatives.getchosenresponse()) )
								{
									newfreq = rs.getInt("b" + rifourteen ) + 1;
									break;
								}
							}
					}
System.out.println("RANDOM2");
					stmt.executeUpdate("UPDATE t_lines SET b" + rifourteen + "=\"" + newfreq + "\" where flinenum=\"" + s + "\"");//add new frequency
				}
				catch (SQLException e )
				{
					System.out.println("Oh no! 22");
				}
		}
		else
		{
			primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
				try
				{
					rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + s + "\"");
					ResultSetMetaData rsmd = rs.getMetaData();
					int numColsfour = rsmd.getColumnCount();
					numColsfour = numColsfour - 2;
					numColsfour = numColsfour / 2;
						while (rs.next())
						{
							Boolean foundlineresponse=false;
							int w = 0;
								for (w=0; w < numColsfour; w++)
								{
									if ( rs.getInt("a" + w) == primatives.getresponseid() )
									{
										int newfreqtwo = rs.getInt("b" + w) + 1;
										stmt.executeUpdate("UPDATE t_lines SET b" + w + "=\"" + newfreqtwo + "\" where flinenum=\"" + s + "\"");//add new frequency
										foundlineresponse=true;
										if (rs.wasNull()) break;
									}
								}

								if ( foundlineresponse == false)
								{
System.out.println("gothere1");
									Boolean foundempty=false;
									w = 0;
										for (w=0; w < numColsfour; w++)
										{
											rs.getInt("a"+w);
												if (rs.wasNull())
												{
													stmt.executeUpdate("UPDATE t_lines SET a" + w + "=\"" + primatives.getresponseid() + "\" where flinenum=\"" + s + "\"");
													stmt.executeUpdate("UPDATE t_lines SET b" + w + "=\"1\" where flinenum=\"" + s + "\"");
													foundempty=true;
System.out.println("gottere2");
													break;
												}
											break;
										}

										if ( foundempty == false )
										{
System.out.println("gotthere3");
											stmt.executeUpdate("ALTER TABLE t_lines ADD a" + numColsfour + " INT");
											stmt.executeUpdate("ALTER TABLE t_lines ADD b" + numColsfour + " INT");
System.out.println("gotthere4");
											stmt.executeUpdate("UPDATE t_lines SET a" + numColsfour + "=\"" + primatives.getresponseid() + "\" where flinenum=\"" + s + "\"");
System.out.println("gotthere5");
											stmt.executeUpdate("UPDATE t_lines SET b" + numColsfour + "=\"1\" where flinenum=\"" + s + "\"");
										}
								}
							break;
						}
				}
				catch (SQLException e )
				{
				System.out.println("Oh no!hehooha");
				}
		}
	}

	void printstartofhtml(PrintWriter out)
	{
		out.println("<html>");
		out.println("<body>");
		out.println("<center>");
		out.println("Now choose an existing response:");
		out.println("<br>");
		out.println("<br>");
		out.println("<form name=one method=post>");
		out.println("<select name=existingresponse size=13 style=text-align:center;>");
	}

	void printendofhtml(PrintWriter out)
	{
		out.println("</select>");
		out.println("<br>");
		out.println("<input type=submit name=button1 value=\"Submit Existing Response\" />");
		out.println("</form>");
		out.println("Or enter a new one:");
		out.println("<br>");
		out.println("<br>");
		out.println("<form name=two method=post>");
		out.println("<input type=text name=newresponse />");
		out.println("<br>");
		out.println("<input type=submit name=button2 value=\"Submit New Response\" />");
		out.println("</form>");
		out.println("</center>");
		out.println("</body>");
		out.println("</html>");
	}

	void thanksforcontributing(PrintWriter out)
	{
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=refresh content=2;url=http://publicsite.org:8080>");
		out.println("</head>");
		out.println("<center>");
		out.println("<h1>");
		out.println("Thank you for contributing!");
		out.println("</h>");
		out.println("</center>");
		out.println("</html>");
	}

// -----------------------------------LOOP STARTS HERE--------------------------------------

	void loop(HttpServletRequest req, HttpServletResponse res, Connection conn, Statement stmt, ResultSet rs) throws IOException
	{

	PrintWriter out = res.getWriter();

	Primatives primatives = new Primatives(-1, -1, -1, -1, "");

	ArrayList<Integer> vresponseindexs = new ArrayList<Integer>(); //no
	ArrayList<Integer> vlinefreq = new ArrayList<Integer>(); //no

	ArrayList<Integer> vchecklinenum = new ArrayList<Integer>();//no
	ArrayList<Integer> vcheckstartpos = new ArrayList<Integer>(); //no

	ArrayList<Integer> vtemplinenum = new ArrayList<Integer>(); //no
	ArrayList<Integer> vtemplinenumfreq = new ArrayList<Integer>(); //no

	ArrayList<Integer> vlinepatterns = new ArrayList<Integer>(); //no
	ArrayList<Integer> vlinepatternfreq = new ArrayList<Integer>(); //no

	ArrayList<Integer> vaddlinepatterns = new ArrayList<Integer>(); //no
	ArrayList<Integer> vaddlinepatternfreq = new ArrayList<Integer>(); //no

	ArrayList<String> vaddpatterns = new ArrayList<String>(); //no
	ArrayList<ArrayList<Integer>> vaddlinenum = new ArrayList<ArrayList<Integer>>(); //no
	ArrayList<ArrayList<Integer>> vaddlinenumfreq = new ArrayList<ArrayList<Integer>>(); //no

	ArrayList<Integer> vaddpatternnum = new ArrayList<Integer>(); //no
	ArrayList<ArrayList<Integer>> vaddlinenumtwo = new ArrayList<ArrayList<Integer>>(); //no
	ArrayList<ArrayList<Integer>> vaddlinenumfreqtwo = new ArrayList<ArrayList<Integer>>(); //no

	ArrayList<Integer> voutputlinenums = new ArrayList<Integer>();
	ArrayList<Integer> voutputlinenumfreq = new ArrayList<Integer>();

		Boolean addornot;
		Boolean proceed;

		//GET LINENUM
			try
			{
				rs = stmt.executeQuery("SELECT MAX( flinenum ) FROM t_lines");
				while (rs.next())
				{
					primatives.setlinenum(rs.getInt(1));
				}
			}
			catch (SQLException e )
			{
				System.out.println("Oh no! 1");
			}

		primatives.setlinenum(primatives.getlinenum() + 1);

		//System.out.println("Enter speech");
		String line = req.getParameter("speech");

		if(line != null)
		{
			int s = -1;
			try
			{
				rs = stmt.executeQuery("SELECT * FROM t_lines WHERE BINARY(flines) = \"" + line + "\""); // NUMBER 1
				while (rs.next())
				{
					if (!rs.wasNull()) s = rs.getInt("flinenum");
				}
				//System.out.println(s);
			}
			catch (SQLException e )
			{
				System.out.println("Oh no! 21");
			}

			if ( s != -1 )
			{ //if the line already exists
				vresponseindexs.clear();
				//vlinefreq.clear();

				addtolinefreq(vlinefreq, conn, stmt, rs, s); //fill vlinefreq with vfrequency data from the line.
				
				printstartofhtml(out); //print start of html
				listbyfreq(out, vresponseindexs, vlinefreq, s, conn, stmt, rs); // list all responses in checkbox and add to them to vresponseindexs
				printendofhtml(out); //print end of html

				addornot=getoperant(primatives, vresponseindexs.size());

				HttpSession session = req.getSession(true);
				session.putValue("h.primatives", primatives);
				session.putValue("h.vresponseindexs", vresponseindexs);
				session.putValue("h.s", s);

				processexistingline(primatives, vresponseindexs, conn, stmt, rs, s, addornot);

			}
			else
			{ // if the line has not been found
				System.out.println("doesnt exist");

				int h;
					for (h = 1; h <= line.length(); h++)
					{
						vchecklinenum.clear();
						vcheckstartpos.clear();
						int i;
							for (i = h; i <= line.length(); i++)
							{
								int startpos=h-1;
								int k=i; // CAN BE SIMPLIFIED!!!

									int l = -1;
									try
									{
										rs = stmt.executeQuery("SELECT * FROM t_patterns WHERE BINARY(fpatterns) = '" + line.substring(startpos,k) + "'");
											while (rs.next())
											{
												if (!rs.wasNull()) l = rs.getInt("fpatternnum"); else l = -1;
											}
									}
									catch (SQLException e )
									{
										System.out.println("Oh no! 3");
									}

									if ( l != -1 )
									{ // FOUND
//System.out.println("l!=-1 ... ¬" + line.substring(startpos,k) + "¬");
									//ADD PATTERN HERE!!!
										//ADD ALL LINES FOR PATTERN TO VCHECKLINENUM
										vchecklinenum.clear();
										vcheckstartpos.clear();

											try
											{
												rs = stmt.executeQuery("SELECT * FROM t_patterns WHERE fpatternnum = \"" + l + "\"");
												ResultSetMetaData rsmd = rs.getMetaData();
												int  w = rsmd.getColumnCount();
												w = w - 2;
												w = w / 2;
												int y = 0;
													while (rs.next())
													{
															for ( y = 0; y < w; y++)
															{ // GO THROUGH VLINENUM
																int x = rs.getInt("a" + y);
																	if (!rs.wasNull())
																	{
																		//System.out.println("--->" + rs.getString("fpatterns") + "<->" + x);
																		vchecklinenum.add(x);
																		vcheckstartpos.add(0);
																	}
																	else break;
															}
														break;
													}
											}
											catch (SQLException e )
											{
											System.out.println("Oh no!x");
											}

												processlinepattern(vlinepatterns, vlinepatternfreq, l);

												//rs.beforeFirst();
												//wibble
												int riten;
												Boolean foundpatternnuminarray = false;
												for (riten=0; riten < vaddpatternnum.size(); riten++)
												{
													if ( vaddpatternnum.get(riten) == l )
													{
														foundpatternnuminarray = true;
															Boolean foundalreadytwo = false;
															int rieleven;
															for (rieleven=0; rieleven < vaddlinenumtwo.get(riten).size(); rieleven++)
															{
																if ( primatives.getlinenum() == vaddlinenumtwo.get(riten).get(rieleven) );
																{
																	foundalreadytwo = true;
																	vaddlinenumfreqtwo.get(riten).set(rieleven, vaddlinenumfreqtwo.get(riten).get(rieleven) + 1);
																	break;
																}
															}

															if (foundalreadytwo == false)
															{
																vaddlinenumtwo.get(riten).add(primatives.getlinenum());
																vaddlinenumfreqtwo.get(riten).add(1);
															}
														break;
													}
												}

												if ( foundpatternnuminarray == false )
												{
													vaddpatternnum.add(l);
													vaddlinenumtwo.add(new ArrayList<Integer>());
													vaddlinenumtwo.get(vaddlinenumtwo.size() - 1).add(primatives.getlinenum());
													vaddlinenumfreqtwo.add(new ArrayList<Integer>());
													vaddlinenumfreqtwo.get(vaddlinenumfreqtwo.size() - 1).add(1);
												}
									}
									else
									{ //if l == -1 NOT FOUND
//System.out.println("l==-1 ... ¬" + line.substring(startpos,k) + "¬");
											Boolean foundpatterninarray = false;
											int ritwo;
											for (ritwo=0; ritwo < vaddpatterns.size(); ritwo++)
											{ //Search through patterns in array

												if ( vaddpatterns.get(ritwo).equals(line.substring(startpos,k)))
												{ // If pattern is found in array

													foundpatterninarray = true;

													//ADD ALL LINES FOR PATTERN TO VCHECKLINENUM
													vchecklinenum.clear();
													vcheckstartpos.clear();
													vchecklinenum=new ArrayList<Integer>(vaddlinenum.get(ritwo));
													int rinine;
													for (rinine = 0; rinine < vchecklinenum.size(); rinine++) vcheckstartpos.add(0);

													processaddlinepattern(vaddlinepatterns, vaddlinepatternfreq, ritwo);

													// ADD THE PATTERN TO VADDLINENUM OR INCREASE FREQUENCY IF IT ALREADY EXISTS

													Boolean foundalready = false;
													int rithree;
														for (rithree=0; rithree < vaddlinenum.get(ritwo).size(); rithree++)
														{
															if ( vaddlinenum.get(ritwo).get(rithree) == primatives.getlinenum() )
															{
																vaddlinenumfreq.get(ritwo).set(rithree,vaddlinenumfreq.get(ritwo).get(rithree) + 1);
																foundalready=true;
																break;
															}
														}

														if (foundalready == false)
														{
															vaddlinenum.get(ritwo).add(primatives.getlinenum());										
															vaddlinenumfreq.get(ritwo).add(1);
														}
													break;
												}
											}

										if (foundpatterninarray == false)
										{
//System.out.println("it's kinda working!");
											vtemplinenum.clear();
											vtemplinenumfreq.clear();

												if ( vchecklinenum.size() == 0 )
												{
														try
														{
															rs = stmt.executeQuery("SELECT * FROM t_lines WHERE BINARY(flines) LIKE '%" + line.substring(startpos,k) + "%'");
																while (rs.next())
																{
																	int position = 0;
																	while (position >= 0 )
																	{
																		String astring = rs.getString("flines");
																		if (!rs.wasNull()) position=astring.indexOf(line.substring(startpos,k),position);
																			if ( position >= 0 )
																			{	

																				processtemplinenum(vtemplinenum, vtemplinenumfreq, rs.getInt("flinenum"));

																				vchecklinenum.add(rs.getInt("flinenum"));
																				vcheckstartpos.add(position);

																				position=position+1;
																			}
																	}
																}
														}
														catch (SQLException e )
														{
															System.out.println("Oh no! 4");
														}

														if ( vtemplinenum.size() > 0 )
														{
//System.out.println("YES1");
															processtemplinenum(vtemplinenum, vtemplinenumfreq, primatives.getlinenum());
															vaddlinenum.add(new ArrayList<Integer>(vtemplinenum));
															vaddlinenumfreq.add(new ArrayList<Integer>(vtemplinenumfreq));
															processaddlinepattern(vaddlinepatterns, vaddlinepatternfreq, vaddpatterns.size());
															vaddpatterns.add(line.substring(startpos,k));

															//sortitout(conn, stmt, rs, line, startpos, k);
														}
														else break;
												}
												else
												{
//System.out.println("¬" + line.substring(startpos,k) + "¬" + vchecklinenum);
													//vtemplinenum.clear();
System.out.println("vchecklinenum= " + vchecklinenum);
System.out.println("vcheckstartpos=" + vcheckstartpos);
													// SEARCH THROUGH PATTERNS IN vchecklinenum
													int m;
													int position2 = -1;
														for (m = 0; m < vchecklinenum.size(); m++)
														{
//System.out.println(m);	
																try
																{
																	rs = stmt.executeQuery("SELECT * FROM t_lines WHERE flinenum = \"" + vchecklinenum.get(m) + "\"" );
																		while (rs.next())
																		{
//System.out.println("!!!" + rs.getString("flines"));
																			if (!rs.wasNull()) position2=rs.getString("flines").indexOf(line.substring(startpos,k),vcheckstartpos.get(m));
																		}
																}
																catch (SQLException e )
																{
																	System.out.println("Oh no! 8");
																}

																if ( position2 >= 0 )
																{	
//System.out.println("wazzup");
																	processtemplinenum(vtemplinenum, vtemplinenumfreq, vchecklinenum.get(m));
																}
																else //if the a pattern isn't found, do not check instance again.
																{
																	vchecklinenum.remove(m);
																	vcheckstartpos.remove(m);
																	m = m - 1;
																}
														}

														if ( vtemplinenum.size() > 0 )
														{
//System.out.println("YES2");
															processtemplinenum(vtemplinenum, vtemplinenumfreq, primatives.getlinenum());
															vaddlinenum.add(new ArrayList<Integer>(vtemplinenum));
															vaddlinenumfreq.add(new ArrayList<Integer>(vtemplinenumfreq));
															processaddlinepattern(vaddlinepatterns, vaddlinepatternfreq, vaddpatterns.size());
															vaddpatterns.add(line.substring(startpos,k));

															//sortitout(conn, stmt, rs, line, startpos, k);
														}
														else break;
												}
										}
									}
							}
					}

				// START OF OUTPUT

				ArrayList<Integer> vtempresponses = new ArrayList<Integer>();
				ArrayList<Integer> vtempfrequency = new ArrayList<Integer>();

					if ( vlinepatterns.size() == 0 && vaddlinepatterns.size() == 0)
					{ // If there are no patterns

						printstartofhtml(out);
						out.println("<option selected=true value=0>" + line + "</option>");
						printendofhtml(out);

						////addornot = getoperant(primatives, 0);

						HttpSession session = req.getSession(true);
						String sessionline = new String(line);
						session.putValue("h.line", sessionline);
						session.putValue("h.primatives", primatives);
						session.putValue("h.vresponseindexs", vresponseindexs);
						Boolean trueorfalse = new Boolean(true);
						session.putValue("h.trueorfalse", trueorfalse);

						////proceed=addline(primatives,vresponseindexs, conn, stmt, rs, line, addornot, true);

							////if ( proceed == true )
							////{
								////if ( addornot == true )
								////{
									////primatives.setresponseid(processresponse(line, conn, stmt, rs));
									////addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
							////	}
								////else
							////	{
								////	primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
								////	addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
								////}
						////	}
					}
					else
					{
						int rifive = 0; //n
						for (rifive=0; rifive < vaddlinepatterns.size(); rifive++)
						{
							int risix = 0; //u
							for (risix=0; risix < vaddlinepatternfreq.get(rifive); risix++)
							{
								int riseven = 0; //o
								for (riseven=0; riseven < vaddlinenum.get(vaddlinepatterns.get(rifive)).size(); riseven++)
								{ // for each line
//System.out.println("it got this far.");
									if ( vaddlinenum.get(vaddlinepatterns.get(rifive)).get(riseven) != primatives.getlinenum() )
									{
//System.out.println("it got this far2.");
										int differenceone = vaddlinenumfreq.get(vaddlinepatterns.get(rifive)).get(riseven) - risix;
//System.out.println("it got this far3.");
										if ( differenceone > 0 )
										{
											//ADD TO voutputlinenums OR INCREASE FREQ.

												int rieight = 0; //v
												Boolean foundoutputlinenumone = false;
												for (rieight=0; rieight < voutputlinenums.size(); rieight++)
												{
													if (voutputlinenums.get(rieight) == vaddlinenum.get(vaddlinepatterns.get(rifive)).get(riseven))
													{
														voutputlinenumfreq.set(rieight,voutputlinenumfreq.get(rieight) + 1);
														foundoutputlinenumone=true;
													}
												}
	
												if ( foundoutputlinenumone == false )
												{
													voutputlinenums.add(vaddlinenum.get(vaddlinepatterns.get(rifive)).get(riseven));
													voutputlinenumfreq.add(1);
												}
										}
									}
								}
							}
						}

						int n = 0;
						for (n=0; n < vlinepatterns.size(); n++)
						{//for each pattern in the line
							int u = 0;
							for (u=0; u < vlinepatternfreq.get(n); u++)
							{
								try
								{
									rs = stmt.executeQuery("SELECT * FROM t_patterns WHERE fpatternnum = \"" + vlinepatterns.get(n) + "\"" );
										while (rs.next())
										{
											ResultSetMetaData rsmd = rs.getMetaData();
											int numColstwo = rsmd.getColumnCount();
											numColstwo = numColstwo - 2;
											numColstwo = numColstwo / 2;
											int o;
												for ( o = 0; o < numColstwo; o++)
												{
													if ( rs.getInt("a"+o) != primatives.getlinenum() )
													{
														int difference = rs.getInt("b"+o) - u;

															if ( difference > 0 )
															{

																//ADD TO voutputlinenums OR INCREASE FREQ.

																int v = 0;
																Boolean foundoutputlinenum = false;
																	for (v=0; v < voutputlinenums.size(); v++)
																	{
																		if (voutputlinenums.get(v) == rs.getInt("a"+o))
																		{
																			voutputlinenumfreq.set(v,voutputlinenumfreq.get(v) + 1);
																			foundoutputlinenum=true;
																		}
																	}

																	if ( foundoutputlinenum == false )
																	{
																		//System.out.println("ADDED1");
																		voutputlinenums.add(rs.getInt("a"+o));
																		voutputlinenumfreq.add(1);
																	}
															}
													}
												}
										}
								}
								catch (SQLException e )
								{
									System.out.println("Oh no!aaah");
								}
							}
						}

						vresponseindexs.clear();

						printstartofhtml(out);
						listlinesbyfreq(out, vresponseindexs, vlinefreq, voutputlinenums, voutputlinenumfreq, conn, stmt, rs);
						printendofhtml(out);

addornot=false;
						////addornot=getoperant(primatives, vresponseindexs.size());

						HttpSession session = req.getSession(true);
						String sessionline = new String(line);
						session.putValue("h.line", sessionline);
						session.putValue("h.primatives", primatives);
						session.putValue("h.vresponseindexs", vresponseindexs);
						Boolean trueorfalse = new Boolean(false); //no patterns = true some patterns = false
						session.putValue("h.trueorfalse", trueorfalse);

						session.putValue("h.vaddpatterns", vaddpatterns);
						session.putValue("h.vaddpatternnum", vaddpatternnum);
						session.putValue("h.vaddlinenum", vaddlinenum);
						session.putValue("h.vaddlinenumtwo", vaddlinenumtwo);
						session.putValue("h.vaddlinenumfreqtwo", vaddlinenumfreqtwo);
						session.putValue("h.vaddlinenumfreq", vaddlinenumfreq);

						proceed=addline(primatives,vresponseindexs, conn, stmt, rs, line, addornot, false);

							if ( proceed == true )
							{
								if ( addornot == true )
								{//If the user enters an existing response
									addresponse(primatives, conn, stmt, rs, vresponseindexs.get(primatives.getchosenresponse()), 1);
									addpatternsone(primatives, vaddpatterns, vaddlinenum, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, vaddlinenumfreq, conn, stmt, rs);
									addpatternstwo(primatives, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, conn, stmt, rs);
								}
								else
								{ //If the user enters a new response.
									primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
									addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
									addpatternsone(primatives, vaddpatterns, vaddlinenum, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, vaddlinenumfreq, conn, stmt, rs);
									addpatternstwo(primatives, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, conn, stmt, rs);
								}
							}
							else
							{
									primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
									addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
							}
					}
			}
		}	
		else if ( req.getParameter("button1") != null )
		{ // if(line == null)
			HttpSession session = req.getSession(true);
			addornot = true;
	    		line = (String)session.getValue("h.line");

			primatives = (Primatives)session.getValue("h.primatives");
			vresponseindexs = (ArrayList<Integer>)session.getValue("h.vresponseindexs");

			try
			{
			primatives.setchosenresponse(Integer.parseInt(req.getParameter("existingresponse")));
  			}
			catch (NumberFormatException nfe)
			{
				System.out.println("NumberFormatException: " + nfe.getMessage());
			}

				if ( line != null )
				{

					Boolean trueorfalse = (Boolean)session.getValue("h.trueorfalse");

					proceed=addline(primatives,vresponseindexs, conn, stmt, rs, line, addornot, trueorfalse);

						if ( proceed == true )
						{
							if ( trueorfalse == true)
							{
								primatives.setresponseid(processresponse(line, conn, stmt, rs));
								addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
							}
							else if (trueorfalse == false)
							{
								vaddpatterns = (ArrayList<String>)session.getValue("h.vaddpatterns");
								vaddpatternnum = (ArrayList<Integer>)session.getValue("h.vaddpatternnum");
								vaddlinenum = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenum");
								vaddlinenumtwo = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumtwo");
								vaddlinenumfreqtwo = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumfreqtwo");
								vaddlinenumfreq = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumfreq");

								addresponse(primatives, conn, stmt, rs, vresponseindexs.get(primatives.getchosenresponse()), 1);
								addpatternsone(primatives, vaddpatterns, vaddlinenum, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, vaddlinenumfreq, conn, stmt, rs);
								addpatternstwo(primatives, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, conn, stmt, rs);
							}
						}
						else
						{
								primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
								addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
						}
				}
				else if (vresponseindexs != null)
				{
					int s = (Integer)session.getValue("h.s");
					processexistingline(primatives, vresponseindexs, conn, stmt, rs, s, addornot);
				}

			if(session!=null) session.invalidate();
			thanksforcontributing(out);
		}
		else if ( req.getParameter("button2") != null )
		{ // if(line == null) & button 2 is clicked instead

			HttpSession session = req.getSession(true);
			addornot = false;
	    		line = (String)session.getValue("h.line");

			primatives = (Primatives)session.getValue("h.primatives");
			vresponseindexs = (ArrayList<Integer>)session.getValue("h.vresponseindexs");
			primatives.setnewanswer(req.getParameter("newresponse"));

				if ( line != null )
				{
					Boolean trueorfalse = (Boolean)session.getValue("h.trueorfalse");

					proceed=addline(primatives,vresponseindexs, conn, stmt, rs, line, addornot, trueorfalse);
						if ( proceed == true )
						{
							if ( trueorfalse == true)
							{
								primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
								addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
							}
							else if (trueorfalse == false)
							{
								vaddpatterns = (ArrayList<String>)session.getValue("h.vaddpatterns");
								vaddpatternnum = (ArrayList<Integer>)session.getValue("h.vaddpatternnum");
								vaddlinenum = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenum");
								vaddlinenumtwo = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumtwo");
								vaddlinenumfreqtwo = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumfreqtwo");
								vaddlinenumfreq = (ArrayList<ArrayList<Integer>>)session.getValue("h.vaddlinenumfreq");

								primatives.setresponseid(processresponse(primatives.getnewanswer(), conn, stmt, rs));
								addresponse(primatives, conn, stmt, rs, primatives.getresponseid(), 1);
								addpatternsone(primatives, vaddpatterns, vaddlinenum, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, vaddlinenumfreq, conn, stmt, rs);
								addpatternstwo(primatives, vaddpatternnum, vaddlinenumtwo, vaddlinenumfreqtwo, conn, stmt, rs);
							}
						}
				}
				else if (vresponseindexs != null)
				{
					int s = (Integer)session.getValue("h.s");
					processexistingline(primatives, vresponseindexs, conn, stmt, rs, s, addornot);
				}

			if(session!=null) session.invalidate();
			thanksforcontributing(out);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// Set the Content-Type header
		res.setContentType("text/html");

		// Return early if this is a HEAD
		if (req.getMethod().equals("HEAD")) return;

		// Proceed otherwise

		Connection conn = null;
			try
			{
				String userName = "root";
				String password = "YourDatabasePassword";
				String url = "JavaDatabaseConnectivityAPI:RelationDatabaseManagementSystemName://localhost/mrknowitall";
				Class.forName ("com.RelationDatabaseManagementSystemName.JavaDatabaseConnectivityAPI.Driver").newInstance ();
				conn = DriverManager.getConnection (url, userName, password);
				//System.out.println ("Database connection established");

				Statement stmt = conn.createStatement();
				ResultSet rs = null;
				loop(req, res, conn, stmt, rs);
			}
			catch (Exception e)
			{
				System.err.println ("Error message: " + e.getMessage ());
			}
			finally
			{
				if (conn != null)
				{
						try
						{
							conn.close ();
							//System.out.println ("Database connection terminated");
						}
					catch (Exception e) { /* ignore close errors */ }
				}
			}
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doPost(req,res);
	}
}
