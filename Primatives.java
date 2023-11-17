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

public class Primatives{

	private int linenum;
	private int newlinenum;

	private int responseid;
	private int chosenresponse;
	private String newanswer;

	public Primatives(int val, int valtwo, int valthree , int valfour , String valfive){
		linenum = val;
		newlinenum = valtwo;
		responseid = valthree;
		chosenresponse = valfour;
		newanswer = valfive;
	}

// linenum

	public void setlinenum(int val){
		this.linenum = val;
	}
	public int getlinenum(){
		return linenum;
	}

// newlinenum

	public void setnewlinenum(int val){
		this.newlinenum = val;
	}
	public int getnewlinenum(){
		return newlinenum;
	}

//responseid

	public void setresponseid(int val){
		this.responseid = val;
	}
	public int getresponseid(){
		return responseid;
	}

//chosenresponse

	public void setchosenresponse(int val){
		this.chosenresponse = val;
	}
	public int getchosenresponse(){
		return chosenresponse;
	}

//newanswer

	public void setnewanswer(String val){
		this.newanswer = val;
	}
	public String getnewanswer(){
		return newanswer;
	}
}