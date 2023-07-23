package processor.memorysystem;

public class cacheLine
{
	int tag;
	int data;
	public cacheLine()
	{
		tag = -1;
		data = -1;
	}
	public void setdata(int Data)
	{
		data = Data ;
	}
	public int getdata()
	{
		return data;
	}
	public void settag(int Tag)
	{
		tag = Tag;
	}
	public int gettag()
	{
		return tag;
	}
}
