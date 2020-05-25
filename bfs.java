import java.util.*;
import java.io.*;


//This class contains the explore messages that are to be sent from the marked processes to their neighbours.
class Explore 
{
	Process[] p;
	int n;
	public Explore(Process[] pr, int no)
	{
		p=pr;
		n=no;
	}

	//This function contains the response that the neighbours will send when they recieve the explore message

	public boolean isAck(int sender, int recieve)
	{
		if(p[recieve].get_marked())
			return false;
		else
		{
			p[recieve].set_parent(sender);
			p[recieve].set_mark();
			return true;
		}
	}
}

class Process extends Thread
{
	int uid;
	boolean root; //is the process a root or not
	int[] neighbour; //1 if process i is a neighbour and 0 otherwise
	int parent;//who is my parent process?
	int[] children;//which processes are my children?
	boolean mark;
	int leaf;
	Process[] p;
	int n;

	public Process(int id, boolean roots, int[] neighbours, int no)
	{
		n=no;
		uid=id;
		root=roots;
		neighbour=neighbours;
		mark=false;
		leaf=-1;
		if(root==true)
		{
			mark=true;
			parent=uid;
			leaf=0;
		}
		children=new int[n];
		for(int i=0;i<n;i++)
			children[i]=0;
	}
	public void set_process(Process[] pr)
	{
		p=pr;
	}
	public void set_leaf(int val)
	{
		leaf=val;
	}
	public int get_leaf()
	{
		return leaf;
	}
	public boolean get_marked()
	{
		return mark;
	}
	public void set_mark()
	{
		mark=true;
	}
	public void set_child(int i)
	{
		children[i]=1;
	}
	public void set_parent(int par)
	{
		parent=par;
	}
	public int[] get_children()
	{
		return children;
	}
	public void execute()
	{
		if(get_marked())
		{
			for(int i=0;i<n;i++)
			{
				if(neighbour[i]==1)
				{
					Explore e=new Explore(p,n);
					Random r=new Random();
					int wait_time=r.nextInt(15);
					try
					{
						Thread.sleep(wait_time);
					}
					catch(Exception ex)
					{
						System.out.println(ex);
					}
					boolean ack=e.isAck(uid,i);
					if(ack)
					{
						set_child(i);
						System.out.println(uid+" --> "+i);
						set_leaf(0);
					}
					else
						set_leaf(1);
				}
			}
		}
	}
	public void run()
	{
		//System.out.println("Process with id "+uid+" has been started");
		if(root)
		{
			for(int i=0;i<n;i++)
			{
				if(neighbour[i]==1)
				{
					Explore e=new Explore(p,n);
					Random r=new Random();
					int wait_time=r.nextInt(15);
					try
					{
						Thread.sleep(wait_time);
					}
					catch(Exception ex)
					{
						System.out.println(ex);
					}
					boolean ack=e.isAck(uid,i);
					if (ack)
					{
						set_child(i);
						System.out.println(uid+" --> "+i);
					}
				}
			}
		}
	}
}

class bfs
{
	public static void main(String args[])
	{
		try 
		{
			FileReader file = new FileReader("input.txt");
			BufferedReader f= new BufferedReader(file);
			String no=f.readLine();
			int n=Integer.parseInt(no);
			String ro=f.readLine();
			int root=Integer.parseInt(ro);
			System.out.println("n = "+n+"  root = "+root);
			int[][] trans=new int[n][n];
			for(int i=0;i<n;i++)
			{
				String t=f.readLine();
				StringTokenizer tokenizer = new StringTokenizer(t, ", ");
				int j=0;
				while (tokenizer.hasMoreTokens())
				{
					trans[i][j]=Integer.parseInt(tokenizer.nextToken());
					j+=1;
				}
			}

			Process[] p=new Process[n];
			for(int i=0;i<n;i++)
			{
				p[i]=new Process(i,(i==root),trans[i],n);
				//p[i].start();
			}

			System.out.println("BFS Tree : ");

			for(int i=0;i<n;i++)
			{
				p[i].set_process(p);
				p[i].start();
			}

			boolean term=false;
			while(!term)
			{
				for(int i=0;i<n;i++)
					p[i].execute();
				term=true;
				for(int i=0;i<n;i++)
					if(p[i].get_leaf()==-1)
						term=false;
			}

			
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File Not Found");
		}
		catch(IOException e)
		{
			System.out.println("Invalid input");
		}
		catch(Exception e)
		{
			System.out.println("Process Interupted");
		}
	}
}