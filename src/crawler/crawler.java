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
		String website="https://www.ptt.cc/bbs/Beauty/index.html" ;//ptt beauty最新一頁
		Searchpage(website) ;
	}
	public static void Searchpage(String website)//多個文章的網頁
	{
		int counter=0 ;	
		while(true)
		{		
			try
			{
				String beautyIndexPage="https://www.ptt.cc/bbs/Beauty/index%s.html" ;
				Integer loadLastPosts=50 ;
				Document document=Jsoup.connect(website).get() ;
				String prevPage=document.select(".btn-group > a")//取得右上角前一頁內容
						.get(3).attr("href")
						.replaceAll("/bbs/Beauty/index([0-9]+).html","$1") ;
				Integer newestPage=Integer.valueOf(prevPage)+1 ;
				LinkedList<String> newestPostsLink=new LinkedList<String>() ;
				while(loadLastPosts>newestPostsLink.size())
				{
					String currentPage=String.format(beautyIndexPage,newestPage--) ;//將integer改成string格式
					Document doc=Jsoup.connect(currentPage).get() ;
					Elements links=doc.select(".title > a") ;
					for(Element link:links)
					{
						String wrongpage=link.text() ;
						if (!wrongpage.contains("水桶") &&!wrongpage.contains("討論"))//de ptt水桶文
						{
							newestPostsLink.add(link.attr("href")) ;//如果有屬性為herf的網址
						}
					}
					if(counter==0)//去掉公告四篇
					{
						for(int i=0;i<4;i++)
						{
							newestPostsLink.removeLast() ;
						}
					}
				}
				for(String url:newestPostsLink)//將newestPostsLink裡面的網址取出
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
	public static void Imagepage(String website)//進去的單個網頁
	{
		try
		{
			Document document=Jsoup.connect(website).get() ;//抓下網址的內容存在document
			String title=document.title() ;//抓下標題名稱
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
			File uploadFile=new File("C:\\Users\\qwe09\\Desktop\\DownloadFile\\"+title) ;//建立新資料夾
			System.out.println("進入 "+title) ;
			if(!uploadFile.exists())
			{	
				System.out.println("新資料夾建立成功") ;
				uploadFile.mkdirs() ;//如果無此資料夾則新增
			}
			else 
			{	
				System.out.println("建立資料夾失敗") ;
			}
			String Text=document.text() ;
			String Match[]=Text.split(" ") ;//用空格分開他們，然後分別存入
			int count=0 ;
			for (int i=0;i<Match.length;i++)
			{
				if(Match[i].contains("**"))
				{
					continue ;
				}
				else if (Match[i].contains("發信站"))
				{
					break ;
				}
				else if (Match[i].contains(".jpg"))
				{
					DownloadImg(Match[i],title,count) ;//網址，資料夾名稱，圖片名稱
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
	public static void Doublecheck(String Match,String title,int count)//不屬於jpg和png 繼續確認
	{
		try
		{
			Document document=Jsoup.connect(Match).get() ;
			Elements Words=document.getElementsByTag("img") ;//尋找符合img的元素
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
					word2="http:"+word1 ;//幫沒有http的網址加上http
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
	public static void DownloadImg(String Match,String title,int count)//下載圖片
	{
		try
		{
			URL url=new URL(Match) ;//開啟符合的照片網址
			FileOutputStream FOS=new FileOutputStream("C:\\Users\\qwe09\\Desktop\\DownloadFile\\"+title+"\\"+(count+1)+".jpg") ;
			InputStream IS=url.openStream() ;//取得url網址資料，並將資料儲存在IS
			System.out.println("downloading -> 此資料夾第  "+(count+1)+" 張圖片") ;
			byte buffer[]=new byte[16384] ;
			int readLine ;
			while((readLine= IS.read(buffer,0,buffer.length))!=-1)//先讀取再寫入
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

