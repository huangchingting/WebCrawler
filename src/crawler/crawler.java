package crawler ;
import java.io.* ;
import java.net.* ;
import java.util.LinkedList ;
import org.jsoup.Jsoup ;
import org.jsoup.nodes.Document ;
import org.jsoup.select.Elements ;
import org.jsoup.nodes.Element ;

public class crawler
{	
	public static void main(String args[]) throws Exception
	{
		String website="https://www.ptt.cc/bbs/Beauty/index.html" ;//ptt beauty�̷s�@��
		Searchpage(website) ;
	}
	public static void Searchpage(String website)//�h�Ӥ峹������
	{
		int counter=0 ;	
		while(true)
		{		
			try
			{
				String beautyIndexPage="https://www.ptt.cc/bbs/Beauty/index%s.html" ;
				Integer loadLastPosts=50 ;
				Document document=Jsoup.connect(website).get() ;
				String prevPage=document.select(".btn-group > a")//���o�k�W���e�@�����e
						.get(3).attr("href")
						.replaceAll("/bbs/Beauty/index([0-9]+).html","$1") ;
				Integer newestPage=Integer.valueOf(prevPage)+1 ;
				LinkedList<String> newestPostsLink=new LinkedList<String>() ;
				while(loadLastPosts>newestPostsLink.size())
				{
					String currentPage=String.format(beautyIndexPage,newestPage--) ;//�Ninteger�令string�榡
					Document doc=Jsoup.connect(currentPage).get() ;
					Elements links=doc.select(".title > a") ;
					for(Element link:links)
					{
						String wrongpage=link.text() ;
						if (!wrongpage.contains("����") &&!wrongpage.contains("�Q��"))//de ptt�����
						{
							newestPostsLink.add(link.attr("href")) ;//�p�G���ݩʬ�herf�����}
						}
					}
					if(counter==0)//�h�����i�|�g
					{
						for(int i=0;i<4;i++)
						{
							newestPostsLink.removeLast() ;
						}
					}
				}
				for(String url:newestPostsLink)//�NnewestPostsLink�̭������}���X
				{
					counter++ ;
					if(counter==51)
					{
						break ;
					}
					String web="https://www.ptt.cc"+url ;
					System.out.println(web) ;
					Imagepage(web) ;
					
				}
				if(counter==51)
				{
					break;
				}
			}
			catch (MalformedURLException e)
			{
		        e.printStackTrace() ;
			}
			catch (FileNotFoundException e)
			{
		        e.printStackTrace() ;
		    }
			catch (IOException e)
			{
		        e.printStackTrace() ;
		    }
			catch (IllegalStateException e)
			{
		        e.printStackTrace() ;
		    }
			catch (NullPointerException e)
			{
		        e.printStackTrace() ;
		    }
		}
	}
	public static void Imagepage(String website)//�i�h����Ӻ���
	{
		try
		{
			Document document=Jsoup.connect(website).get() ;//��U���}�����e�s�bdocument
			String title=document.title() ;//��U���D�W��
			if(title.contains("<") && title.contains(">") || title.contains(":") || title.contains("*") || title.contains("|") || title.contains(" ") || title.contains("?"))
			{
				title=title.replace("<","") ;
				title=title.replace(">","") ;
				title=title.replace(":","") ;
				title=title.replace("*","") ;
				title=title.replace("|","") ;
				title=title.replace(" ","") ;
				title=title.replace("?","") ;
			}
			File uploadFile=new File("C:\\Users\\qwe09\\Desktop\\DownloadFile\\"+title) ;//�إ߷s��Ƨ�
			System.out.println("�i�J "+title) ;
			if(!uploadFile.exists())
			{	
				System.out.println("�s��Ƨ��إߦ��\") ;
				uploadFile.mkdirs() ;//�p�G�L����Ƨ��h�s�W
			}
			else 
			{	
				System.out.println("�إ߸�Ƨ�����") ;
			}
			String Text=document.text() ;
			String Match[]=Text.split(" ") ;//�ΪŮ���}�L�̡A�M����O�s�J
			int count=0 ;
			for (int i=0;i<Match.length;i++)
			{
				if(Match[i].contains("**"))
				{
					continue ;
				}
				else if (Match[i].contains("�o�H��"))
				{
					break ;
				}
				else if (Match[i].contains(".jpg"))
				{
					DownloadImg(Match[i],title,count) ;//���}�A��Ƨ��W�١A�Ϥ��W��
					count++ ;
				}
				else if(Match[i].contains(".png"))
				{
					DownloadImg(Match[i],title,count) ;
					count++ ;
				}
				else if(Match[i].contains("img"))
				{
					Doublecheck(Match[i],title,count);
					count++;
				}
			}
			count=0 ;
		}
		catch (MalformedURLException e)
		{
	        e.printStackTrace() ;
		}
		catch (FileNotFoundException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IOException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IllegalStateException e)
		{
	        e.printStackTrace() ;
	    }
		catch (NullPointerException e)
		{
	        e.printStackTrace() ;
	    }
	}	
	public static void Doublecheck(String Match,String title,int count)//���ݩ�jpg�Mpng �~��T�{
	{
		try
		{
			Document document=Jsoup.connect(Match).get() ;
			Elements Words=document.getElementsByTag("img") ;//�M��ŦXimg������
			String word1,word2 ;
			for(Element Word:Words)
			{
				word1=Word.attr("src") ;
				if(word1.contains("http:"))
				{
					DownloadImg(word1,title,count) ;	
					break ;
				}
				else
				{
					word2="http:"+word1 ;//���S��http�����}�[�Whttp
					DownloadImg(word2,title,count) ;	
					break ;
				}
			}
		}
		catch (MalformedURLException e)
		{
	        e.printStackTrace() ;
		}
		catch (FileNotFoundException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IOException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IllegalStateException e)
		{
	        e.printStackTrace() ;
	    }
		catch (NullPointerException e)
		{
	        e.printStackTrace() ;
	    }
	}
	public static void DownloadImg(String Match,String title,int count)//�U���Ϥ�
	{
		try
		{
			URL url=new URL(Match) ;//�}�ҲŦX���Ӥ����}
			FileOutputStream FOS=new FileOutputStream("C:\\Users\\qwe09\\Desktop\\DownloadFile\\"+title+"\\"+(count+1)+".jpg") ;
			InputStream IS=url.openStream() ;//���ourl���}��ơA�ñN����x�s�bIS
			System.out.println("downloading -> ����Ƨ���  "+(count+1)+" �i�Ϥ�") ;
			byte buffer[]=new byte[16384] ;
			int readLine ;
			while((readLine= IS.read(buffer,0,buffer.length))!=-1)//��Ū���A�g�J
			{
				FOS.write(buffer,0,readLine) ;
			}
			IS.close() ;
			FOS.close() ;
		}
		catch (MalformedURLException e)
		{
	        e.printStackTrace() ;
		}
		catch (FileNotFoundException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IOException e)
		{
	        e.printStackTrace() ;
	    }
		catch (IllegalStateException e)
		{
	        e.printStackTrace() ;
	    }
		catch (NullPointerException e)
		{
	        e.printStackTrace() ;
	    }
	}
}

