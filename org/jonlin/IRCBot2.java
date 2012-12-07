
package org.jonlin;

import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

import com.cometway.util.*;
import com.cometway.net.*;
import com.cometway.httpd.HTMLStringTools;

import Eliza.*;

/**
 * This is Copyrighted under GPL. Do whatever you want with it,
 * but don't blame me if your shit gets fucked. If you want to
 * complain, I'll probably ignore you, if you want to tell me about
 * a bug in my program, I may or may not fix it (probably will
 * eventually) and thank you for it later.
 *
 * jonlin@tesuji.org
 */
public class IRCBot2 implements Runnable
{
	public static final byte[] PRIVMSG = ("PRIVMSG").getBytes();
	public static final byte[] COLON = (":").getBytes();
	public static final byte[] SPACE = (" ").getBytes();
	public static final byte[] RETURN = ("\r\n").getBytes();
	public static final byte[] NEWLINE = ("\n").getBytes();


	STDINReader stdinReader;
	HTTPThread httpThread;
	FileWriter httpLog;
	//	FileWriter rssLog;
	Thread runThread;
	Socket ircSock;
	//	BufferedReader in;
	//	PushbackInputStream in;
	InputStream in;
	Object ircSync;
	OutputStream out;
	Random r;
	ThreadPool pool;
	byte[] favicon;
	SimpleDateFormat dateFormat;
	//	SimpleDateFormat rssFormat;

	long timestamp;
	long fortuneTime;
	long gameTime;
	long triviaTime;
	long filterTime;
	long idleTime;
	long rollTime;
	long pornTime;
	long statsTime;
	long youtubeTime;
	long pantsuTime;
	long rpsTime;

	long randomTimeout;
	long fortuneTimeout;
	long gameTimeout;
	long triviaTimeout1;
	long triviaTimeout2;
	long filterTimeout;
	long statsTimeout;
	long idleTimeout;
	long floodTimeout;
	long rollTimeout;
	int greetingProbability;
	String lastFloodUser;
	String nearFloodUser1;
	String nearFloodUser2;
	int floodLines;
	int hFloodLength;
	int hFloodLines;
	int floodMultiplier;
	int voiceTimeout;
	long pornTimeout;
	int autojoinTimeout;
	int joinSpamTimeout;
	int nickTimeout;
	int analNickTimeout;
	int youtubeTimeout;
	int pantsuTimeout;
	int capsLimit;
	int loliTimeout;
	int trendTimeout;
	long trendTime;
	String lastTrend;
	int rpsTimeout;
	int failedTriggerTimeout;
	int triviaBotTimeout;
	long triviaBotTime;

	Vector triviaQuestions;
	Vector triviaAnswers;
	String lastAnswer;
	Vector recentQuestions;
	Vector alsImages;

	Hashtable filterWordTimeouts;
	String filterWord;
	Vector filterVector;
	String filterFile;
	Vector kickWords;
	Vector addedKickWords;
	String randomMessage;

	HTTPLoader loader;

	//	String[] rssMatch;
	//	Vector rssThreads;
	String lastRSSURL = "http://evangeliwon.blog107.fc2.com/blog-entry-1045.html";  // for testing
	IRCBot2Rss rss;
	GainaxToppageThread toppage;

	String fortuneProgram;
	String triviaFile;
	String scriptsDir;
	String myNick;
	String myUser;
	String myPong;
	String myChan;
	String ircServer;
	int ircPort;
	String adminUser;
	String lastTriggerUser;
	String lastResponseUser;

	Hashtable scriptStats;
	Hashtable triggerStats;
	Hashtable responsesStats;
	Hashtable fortuneStats;
	Hashtable fortuneDatabaseStats;
	Hashtable gameStats;
	Hashtable triviaStats;
	Hashtable answerStats;
	Hashtable rollStats;
	Hashtable ballStats;
	Hashtable msgStats;
	Hashtable youtubeStats;
	Hashtable pantsuStats;
	Hashtable boobiesStats;
	Hashtable channelStats;
	Hashtable channelCapsStats;
	Hashtable joins;
	String lastCaps;
	Hashtable channelLinksStats;
	String lastLinks;
	Hashtable channelShortStats;
	String lastShort;
	Hashtable channelLongStats;
	String lastLong;
	Hashtable channelQuestionStats;
	String lastQuestion;
	Hashtable channelJoinStats;
	String lastJoin;
	Hashtable channelEmoticonStats;
	String lastEmoticon;
	Hashtable idleStats;
	String channelStatsDate;
	int dotStats;
	long lastMostInChannel;
	int mostInChannel;
	Hashtable rpsStats;
	Hashtable recentFilters;

	Hashtable userList;
	NickHash nickTranslation;
	Vector shitList;
	boolean nickChangeKick;
	String lastNickChangeKick;
	long lastNickChange;
	long lastNickChangeKickTime;
	Vector timeoutTriggers;

	//	Vector pastes;
	String pastesDir;
	Vector recent;
	Vector recentVoice;
	int recentSize;

	boolean registered;
	String password;

	String topic;

	Vector netsplit;

	Vector kickAttempts;

	Vector pantsuBlacklist;
	Vector boobiesBlacklist;

	boolean joinTosho;
	boolean joinedTosho;
	Vector matchPatterns;
	int lastMatch;

	LoliTable loliTable;
	String loliMessage;
	String triggerAward;
	String kickAward;
	String kickWordAward;
	String topicAward;
	String loliMessageAward;
	String randomAward;
	Hashtable triggerAwards;
	Hashtable kickAwards;
	Hashtable kickWordAwards;
	Hashtable topicAwards;
	Hashtable voiceAwards;
	Hashtable loliMessageAwards;
	Hashtable randomAwards;
	Hashtable halfopsAwards;
	Hashtable worthTable;
	long lastWorthAdjustment;
	String lastWorthless;
	int loliDiff;
	int loliKillBase;
	int loliKillRoll;
	double loliKillMult;
	BuyTable buyTable;

	String[] japanese;

	//	ElizaMain eliza;
	Vector elizaVector;

	// for formatting:
	char BOLD = ((char)2);
	char UNDERLINE = ((char)31);
	char END = ((char)3);
	String RED = END+"4";
	String BLUE = END+"1";
	String GREEN = END+"2";
	String CYAN = END+"3";
	String PURPLE = END+"5";


	// for services
	boolean nickChanged;
	boolean needRelease;
	boolean needGhost;


	Hashtable triviaBotAnswers;



	public IRCBot2(String[] args)
	{
		runThread = new Thread(this);
		runThread.setName("Main IRC Thread");
		pool = new ThreadPool(30);
		r = new Random();
		timestamp = System.currentTimeMillis();
		fortuneTime = 0;
		filterTime = 0;
		gameTime = 0;
		triviaTime = 0;
		idleTime = System.currentTimeMillis();
		trendTime = System.currentTimeMillis();

		randomTimeout = 10000000;
		fortuneTimeout = 5000;
		gameTimeout = 10000;
		triviaTimeout1 = 60000;
		triviaTimeout2 = 30000;
		filterTimeout = 15000;
		statsTimeout = 60000;
		rollTimeout = 1000;
		idleTimeout = 7200000;
		floodTimeout = 3000;
		greetingProbability = 15;
		lastFloodUser = "";
		nearFloodUser1 = "";
		nearFloodUser2 = "";
		floodLines = 6;
		hFloodLength = 200;
		hFloodLines = 2;
		floodMultiplier = 2;
		voiceTimeout = 50000000;
		pornTimeout = 60000;
		autojoinTimeout = 2000;
		joinSpamTimeout = 30000;
		nickTimeout = 10000;
		analNickTimeout = 120000;
		youtubeTimeout = 60000;
		pantsuTimeout = 15000;
		loliTimeout = 3600000;
		trendTimeout = 3600000;
		lastTrend = "";
		rpsTimeout = 20000;
		failedTriggerTimeout = 5000;
		triviaBotTimeout = 3200000;
		triviaBotTime = System.currentTimeMillis();

		fortuneProgram = "/usr/bin/fortune -s";
		triviaFile = "/home/jonlin/egf/anime_trivia";
		scriptsDir = "/home/jonlin/egf/nge_scripts";
		ircServer = "irc.rizon.net";
		ircPort = 6667;
		myNick = "Fuyutsuki";
		myUser = "ornette benito benito.cometway.com :Fuyutsuki";
		myPong = "benito.cometway.com";
		myChan = "#egf";
		adminUser = "jonlin";
		lastTriggerUser = "";
		lastResponseUser = "";
		randomMessage = "Hoverboards are real.";

		filterWordTimeouts = new Hashtable();
		filterWord = "banned";
		filterVector = new Vector();
		filterVector.addElement("and also: phallic symbols");
		filterVector.addElement("Jesus will love you if you give me money");
		filterVector.addElement("What??");
		filterVector.addElement("It's the History Eraser Button, you fool!");
		filterVector.addElement("You're my bitch, now.");
		filterVector.addElement("We're on a mission from God");
		filterVector.addElement("Ignorance is the number one cause of happiness");
		filterVector.addElement("and also: tits");
		filterVector.addElement("Don't blink. It would really suck if you blinked.");
		filterVector.addElement("Toad Sexing kicks ass.");
		filterFile = "triggers.txt";


		japanese = makeJapanese();

		kickWords = new Vector();
		addedKickWords = new Vector();

		userList = new Hashtable();
		nickTranslation = new NickHash();
		//		pastes = new Vector();
		pastesDir = "pastes";
		recent = new Vector();
		recentVoice = new Vector();
		shitList = new Vector();
		netsplit = new Vector();
		kickAttempts = new Vector();
		joins = new Hashtable();
		matchPatterns = new Vector();
		recentSize = 100;
		pantsuBlacklist = new Vector();
		boobiesBlacklist = new Vector();
		loliTable = new LoliTable("loli.stats");
		worthTable = new Hashtable();
		buyTable = new BuyTable();
		triggerAwards = new Hashtable();
		topicAwards = new Hashtable();
		kickAwards = new Hashtable();
		kickWordAwards = new Hashtable();
		voiceAwards = new Hashtable();
		loliMessageAwards = new Hashtable();
		halfopsAwards = new Hashtable();
		randomAwards = new Hashtable();
		timeoutTriggers = new Vector();
		loliDiff = Math.abs(r.nextInt(1200000));
		loliKillBase = 1000;
		loliKillRoll = 26000;
		loliKillMult = 2.7;

		lastMostInChannel = System.currentTimeMillis();

		scriptStats = new Hashtable();
		triggerStats = new Hashtable();
		responsesStats = new Hashtable();
		fortuneStats = new Hashtable();
		fortuneDatabaseStats = new Hashtable();
		gameStats = new Hashtable();
		triviaStats = new Hashtable();
		answerStats = new Hashtable();
		rollStats = new Hashtable();
		ballStats = new Hashtable();
		msgStats = new Hashtable();
		youtubeStats = new Hashtable();
		pantsuStats = new Hashtable();
		boobiesStats = new Hashtable();
		channelStats = new Hashtable();
		channelCapsStats = new Hashtable();
		channelLinksStats = new Hashtable();
		channelShortStats = new Hashtable();
		channelLongStats = new Hashtable();
		channelQuestionStats = new Hashtable();
		channelJoinStats = new Hashtable();
		channelEmoticonStats = new Hashtable();
		rpsStats = new Hashtable();
		idleStats = new Hashtable();
		lastCaps = null;
		lastLinks = null;
		lastShort = null;
		lastLong = null;
		lastQuestion = null;
		lastJoin = null;
		lastEmoticon = null;
		Date tmpDate = new Date();
		dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss ZZZZZ");
		//		rssFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
		channelStatsDate = dateFormat.format(tmpDate);
		readStats();

		lastMatch = -1;

		triviaBotAnswers = new Hashtable();
		ircSync = new Object();

		loader = new HTTPLoader();
		loader.charset = "UTF-8";
		loader.setAllowForeignCookies(true);
		//		loader.acceptString = "text/html, image/gif, image/jpeg";
		//		loader.addCookie("PREF=ID=de8806dca8285eea:FF=4:LD=en:NR=10:TM=1190114713:LM=1211781064:FV=2:S=tYlBgqoWgrTHTF2H; expires=Wed, 26-May-2010 06:03:18 GMT; path=/; domain=images.google.com");

		for(int x=0;x<args.length;x++) {
			if(args[x].equals("-trivia")) {
				triviaFile = args[++x];
			}
			else if(args[x].equals("-scripts")) {
				scriptsDir = args[++x];
			}
			else if(args[x].equals("-fortune")) {
				fortuneProgram = args[++x];
			}
			else if(args[x].equals("-nick")) {
				myNick = args[++x];
			}
			else if(args[x].equals("-user")) {
				myUser = args[++x];
			}
			else if(args[x].equals("-pong")) {
				myPong = args[++x];
			}
			else if(args[x].equals("-channel")) {
				myChan = args[++x];
			}
			else if(args[x].equals("-server")) {
				ircServer = args[++x];
			}
			else if(args[x].equals("-port")) {
				try {
					ircPort = Integer.parseInt(args[++x]);
				}
				catch(Exception e) {e.printStackTrace();}
			}
			else if(args[x].equals("-admin")) {
				adminUser = args[++x];
			}
			else if(args[x].equals("-triggers")) {
				filterFile = args[++x];
			}
			else if(args[x].equals("-password")) {
				password = args[++x];
			}
			else if(args[x].equals("-tosho")) {
				joinTosho = true;
			}
		}


		/*
		  rssMatch = new String[8];
		  rssMatch[0] = "evangelion";
		  rssMatch[1] = "gainax";
		  rssMatch[2] = "asuka langley";
		  rssMatch[3] = "rei ayanami";
		  rssMatch[4] = "shinji ikari";
		  rssMatch[5] = "khara";
		  rssMatch[6] = "\u30f1\u30f4\u30a1\u30f3\u30b2\u30ea\u30f2\u30f3";
		  rssMatch[7] = "\u30a8\u30f4\u30a1\u30f3\u30b2\u30ea\u30aa\u30f3";
		  try {
		  rssLog = new FileWriter("rss-"+System.currentTimeMillis()+".log");
		  }
		  catch(Exception e) {;}
		*/

		/*		rssThreads = new Vector();
				rssThreads.addElement(new RSSThread("http://www.animenewsnetwork.com/all/rss.xml"));
				rssThreads.addElement(new RSSThread("http://www.funimation.com/rss-news-fun.cfm"));
				rssThreads.addElement(new RSSThread("http://www.evageeks.org/feed/"));
				rssThreads.addElement(new RSSThread("http://kurogane.animeblogger.net/feed/"));
				rssThreads.addElement(new RSSThread("http://feeds2.feedburner.com/dannychoo_com_main_article_feed_eng?format=xml"));
				rssThreads.addElement(new RSSThread("http://neweva.blog103.fc2.com/?xml",false));
				rssThreads.addElement(new RSSThread("http://eva2.0.b-ch.com/blog/2_0/index.rdf",false));
				rssThreads.addElement(new RSSThread("http://evangeliwon.blog107.fc2.com/?xml",false));
				rssThreads.addElement(new RSSThread("http://curieux.blog40.fc2.com/?xml"));
				rssThreads.addElement(new RSSThread("http://www.evastore.jp/blog/?feed=rss",false));
				rssThreads.addElement(new RSSThread("http://mainichi.jp/rss/etc/mantan.rss"));
				rssThreads.addElement(new RSSThread("http://akiba.kakaku.com/index.xml"));
				rssThreads.addElement(new RSSThread("http://ascii.jp/rss.xml"));
				rssThreads.addElement(new RSSThread("http://av.watch.impress.co.jp/sublink/av.rdf"));
				rssThreads.addElement(new RSSThread("http://www.i-mezzo.net/news/index.rdf"));
				rssThreads.addElement(new RSSThread("http://feeds.journal.mycom.co.jp/haishin/rss/index?format=xml"));
				rssThreads.addElement(new RSSThread("http://akibahobby.net/feed"));
				rssThreads.addElement(new RSSThread("http://www.evamonkey.com/feed/",false));
				rssThreads.addElement(new RSSThread("http://evalink.net/comyu/modules/d3pipes/index.php?page=xml&style=rss20"));
				rssThreads.addElement(new RSSThread("http://feeds.feedburner.com/crunchyroll/animenews"));
		*/

		rss = new IRCBot2Rss(this);

		toppage = new GainaxToppageThread();

		FileInputStream fis = null;
		try {
			File icon = new File("favicon.ico");
			fis = new FileInputStream(icon);
			favicon = new byte[(int)icon.length()];
			fis.read(favicon);
		}
		catch(Exception e) {
			System.out.println("Could not read favicon.ico");
		}
		finally {
			try {
				fis.close();
			}
			catch(Exception e) {;}
		}

		elizaVector = new Vector();
			
		loadSettings();
		loadFilters();
		loadTrivia();
		if(!connect()) {
			System.exit(-1);
		}
		runThread.start();
		loadALS();
	}

	public void run()
	{
		String release = null;
		String torrent = null;

		String line = null;
		userList = new Hashtable();

		delay(3000,2000);

		synchronized(out) {
			write("NICK "+myNick);
			write("USER "+myUser);
		}
		try {
			boolean ghosted = false;
			line = readLine(in);
			System.out.println("READING: "+line);
			while(line.indexOf("376 "+myNick)==-1) {
				if(line.startsWith("PING")) {
					fork("pong",line);
				}
				else if(line.indexOf("433 * "+myNick+" :Nickname is already in use.")!=-1) {
					write("NICK "+myNick+"_");
					ghosted = true;
				}
				line = readLine(in);
				System.out.println("READING: "+line);
				if(line.indexOf("PRIVMSG")!=-1) {
					parseCTCP(line,getUser(line));
				}
			}
			if(password!=null) {
				if(ghosted) {
					writeMsg("NickServ","GHOST "+myNick+" "+password);
					while(line.indexOf(":Ghost with your nick has been killed.")==-1) {
						line = readLine(in);
						System.out.println("READING: "+line);
					}
					write("NICK "+myNick);
				}
				while(line.indexOf(myNick+" :This nickname is registered and protected.")==-1) {
					line = readLine(in);
					System.out.println("READING: "+line);
					if(line.indexOf("PRIVMSG")!=-1) {
						parseCTCP(line,getUser(line));
					}
				}
				delay(1000,500);
				writeMsg("NickServ","IDENTIFY "+password);
				while(line.indexOf(myNick+" :Password accepted - you are now recognized")==-1) {
					line = readLine(in);
					System.out.println("READING: "+line);
					if(line.indexOf("PRIVMSG")!=-1) {
						parseCTCP(line,getUser(line));
					}
				}
			}
			//			delay(10000,500);
			write("JOIN "+myChan);
		}
		catch(Exception e) {
			System.out.println("OH SHIT...");
			e.printStackTrace();
			System.exit(-1);
		}

		stdinReader = new STDINReader();
		httpThread = new HTTPThread();
		try {
			httpLog = new FileWriter("http-"+System.currentTimeMillis()+".log");
		}
		catch(Exception e) {;}
		
		write("WHO "+myChan);

		while(true) {
			try {
				try {
					line = readLine(in);
				}
				catch(Exception e) {
					line=null;							
				}
				while(line==null) {
					boolean ghosted = false;
					saveStats();
					try {in.close();}catch(Exception e) {;}
					try {out.close();}catch(Exception e) {;}
					try {ircSock.close();}catch(Exception e) {;}
					ircSock = null;
					if(!connect()) {
						System.exit(-1);
					}
					delay(5000,3000);
					synchronized(out) {
						write("NICK "+myNick);
						write("USER "+myUser);
					}

					line = readLine(in);
					System.out.println("READING: "+line);
					while(line.indexOf("376 "+myNick)==-1) {
						if(line.startsWith("PING")) {
							fork("pong",line);
						}
						else if(line.indexOf("433 * "+myNick+" :Nickname is already in use.")!=-1) {
							write("NICK "+myNick+"_");
							needGhost = true;
						}
						line = readLine(in);
						System.out.println("READING: "+line);
						if(line.indexOf("PRIVMSG")!=-1) {
							parseCTCP(line,getUser(line));
						}
					}
					if(password!=null) {
						claimNick();
					}
					delay(10000,500);
					write("JOIN "+myChan);
					if(joinTosho) {
						write("JOIN #tokyotosho-api");
					}
				}

				if(line.indexOf(":This nickname is registered and protected.")!=-1) {
					delay(2000,500);
					writeMsg("NickServ","IDENTIFY "+password);
				}

				if(line.indexOf(":Ghost with your nick has been killed.")!=-1) {
					needGhost = false;
				}

				if(line.indexOf(" 432 ")!=-1 && line.indexOf(myNick)!=-1 && line.indexOf(":This nick is being held for a registered user.")!=-1) {
					needRelease = true;
					System.out.println("##### NEED RELEASE");
				}

				if(line.indexOf("NOTICE")!=-1 && line.indexOf("Your nickname is now being changed to Guest")!=-1) {
					nickChanged = true;
					System.out.println("##### NICK CHANGED");
				}

				if(line.indexOf("433 * "+myNick+" :Nickname is already in use.")!=-1) {
					needGhost = true;
					System.out.println("##### NEED GHOST");
				}

				if(line.startsWith("ERROR") && line.indexOf(" KILL "+myNick+" :(GHOST command used by "+myNick+")")!=-1) {
					System.out.println("THERE'S 2 COPIES OF ME RUNNING!!! EXITING!!!");
					System.exit(-1);
				}

				if(line.startsWith(":Ornette!~jonlin@tron.tesuji.org PRIVMSG Guest") && line.indexOf("CLAIMNICK")!=-1) {
					claimNick();
				}

				if(nickChanged || needGhost || needRelease) {
					claimNick();
					nickChanged = false;
				}

				String lastTriviaBotQuestion = null;

				String formattedLine = line;
				line = removeFormatting(line);
				String ircName = getIRCName(line);
				String user = getUser(line);
				boolean shitUser = isJavaClient(ircName) || (nickTranslation.containsKey(ircName) && shitList.contains(((String)nickTranslation.get(ircName)).toLowerCase())) || shitList.contains(user.toLowerCase());
				if(line.indexOf(" 352 ")==-1 && line.indexOf(" 315 ")==-1 && line.indexOf(" QUIT :")==-1 && line.indexOf(" NICK :")==-1) {
					if(!joinTosho || (joinTosho && (line.toLowerCase().indexOf("#tokyotosho")==-1 || line.startsWith(":TokyoTosho!")))) {
						System.out.println("READING: "+line);
					}
				}
				if(line.toLowerCase().indexOf(" privmsg "+myChan.toLowerCase()+" :")!=-1) {
					idleTime = System.currentTimeMillis();
					if(user.indexOf(" ")==-1 && user.trim().length()>0 && !user.equals(myNick)) {
						if(!isJavaClient(ircName)) {
							fork("updateStats",user,ircName,"channel");
							if(line.indexOf("...")!=-1) {
								dotStats++;
							}
							String content = line.substring(line.toLowerCase().indexOf(myChan.toLowerCase()+" :") + (myChan+" :").length());
							if(content.equals(content.toUpperCase()) && !content.equals(content.toLowerCase())) {
								fork("updateStats",user,ircName,"channelCaps");
								lastCaps = "&lt;"+user+"&gt; "+content;
							}
							if(content.indexOf("http://")!=-1 || content.indexOf("https://")!=-1) {
								fork("updateStats",user,ircName,"channelLinks");
								lastLinks = "&lt;"+user+"&gt; "+content;
							}
							if(content.length()<5) {
								fork("updateStats",user,ircName,"channelShort");
								lastShort = "&lt;"+user+"&gt; "+content;
							}
							else if(content.length()>150) {
								fork("updateStats",user,ircName,"channelLong");
								lastLong = "&lt;"+user+"&gt; "+content;
							}
							if(content.indexOf("?")!=-1) {
								fork("updateStats",user,ircName,"channelQuestion");
								lastQuestion = "&lt;"+user+"&gt; "+content;
							}
							if(content.indexOf(":)")!=-1 ||
								content.indexOf(";)")!=-1 ||
								content.indexOf("8)")!=-1 ||
								content.indexOf(":|")!=-1 ||
								content.indexOf(">:)")!=-1 ||
								content.indexOf(">:(")!=-1 ||
								content.indexOf(">:D")!=-1 ||
								content.indexOf(">:/")!=-1 ||
								content.indexOf(":P")!=-1 ||
								content.indexOf(":p")!=-1 ||
								content.indexOf(";P")!=-1 ||
								content.indexOf(";p")!=-1 ||
								content.indexOf("=|")!=-1 ||
								content.indexOf(">=)")!=-1 ||
								content.indexOf(">=(")!=-1 ||
								//							content.indexOf("=P")!=-1 ||
								//							content.indexOf("=p")!=-1 ||
								content.indexOf(":(")!=-1 ||
								content.indexOf("=(")!=-1 ||
								content.indexOf("8(")!=-1 ||
								content.indexOf("<_<")!=-1 ||
								content.indexOf(">_>")!=-1 ||
								content.indexOf("-_-")!=-1 ||
								content.indexOf("^_^")!=-1 ||
								content.indexOf("^_-")!=-1 ||
								content.indexOf("-_^")!=-1 ||
								content.indexOf("<.<")!=-1 ||
								content.indexOf(">.>")!=-1 ||
								content.indexOf("-.-")!=-1 ||
								content.indexOf("^.^")!=-1 ||
								content.indexOf("^.-")!=-1 ||
								content.indexOf("-.^")!=-1 ||
								content.indexOf("o_o")!=-1 ||
								content.indexOf("O_O")!=-1 ||
								content.indexOf("o_O")!=-1 ||
								content.indexOf("O_o")!=-1 ||
								content.indexOf("('3')")!=-1 ||
								content.indexOf("(' 3')")!=-1 ||
								content.indexOf("('_')")!=-1 ||
								content.indexOf("('.')")!=-1 ||
								content.indexOf("(`_`)")!=-1 ||
								content.indexOf("(`.`)")!=-1 ||
								content.indexOf("-_#")!=-1 ||
								content.indexOf("#_-")!=-1 ||
								content.indexOf("x_x")!=-1) {
								fork("updateStats",user,ircName,"channelEmoticon");
								lastEmoticon = "&lt;"+user+"&gt; "+content;
							}
						}
					}
					//					fork("addRecent",user,formattedLine,ircName);
					addRecent(user,formattedLine,ircName,true);
				}

				if(line.indexOf(" ")!=-1 && line.substring(line.indexOf(" ")).startsWith(" PRIVMSG "+myNick+" :")) {
					idleTime = System.currentTimeMillis();
					if(!parseCTCP(formattedLine, user)) {
						if(userList.containsKey(user.toLowerCase())) {
							if(!shitUser) {
								fork("parseMsg",formattedLine,user,ircName);
							}
						}
					}
				}
				else if(line.toLowerCase().indexOf("352 "+myNick.toLowerCase()+" "+myChan.toLowerCase())!=-1) {
					int index = line.toLowerCase().indexOf(myChan.toLowerCase());
					if(index!=-1) {
						line = line.substring(index+myChan.length());
						index = line.indexOf("*");
						if(index!=-1) {
							String ircName2 = line.substring(0,index).trim();
							if(ircName2.indexOf(" ")!=-1) {
								ircName2 = ircName2.substring(0,ircName2.indexOf(" "))+"@"+ircName2.substring(ircName2.indexOf(" ")+1);
							}
							line = line.substring(index+1).trim();
							index = line.indexOf(" ");
							if(index!=-1) {
								line = line.substring(0,index);
								if(!isJavaClient(ircName2) && !line.equalsIgnoreCase(myNick)) {
									userList.put(line.toLowerCase(),ircName2);
									if(!nickTranslation.containsKey(ircName2)) {
										System.out.println("Adding to nickTranslation: "+ircName2+"="+line);
										nickTranslation.put(ircName2,line);
									}
								}
							}
						}
					}
				}
				else if(line.toLowerCase().indexOf("315 "+myNick.toLowerCase()+" "+myChan.toLowerCase())!=-1) {
					fork("updateUserList","");
					System.out.println("USERLIST="+userList);
					if(mostInChannel < userList.size()) {
						mostInChannel = userList.size();
						lastMostInChannel = System.currentTimeMillis();
					}
				}
				else if(line.toLowerCase().indexOf("332 "+myNick.toLowerCase()+" "+myChan.toLowerCase())!=-1) {
					int index = line.toLowerCase().indexOf(myChan.toLowerCase()+" :");
					if(index!=-1) {
						line = line.substring(index+myChan.length()+2);
						//						topic = line.trim();
						topic = "EvaGeeks.org http://www.evageeks.org/ - use /msg for SPOILERS - DON'T PASTE INTO THE CHANNEL, http://benito.cometway.com:8000/paste - 3.0 spoilers goes here: #egf-rebuild, for people new to IRC: http://help.dal.net/irchelp/annoyance.php";
					}
				}
				else if(line.startsWith("PING")) {
					fork("pong",line);
					if(System.currentTimeMillis() - idleTime > idleTimeout) {
						int rand = Math.abs(r.nextInt(3));
						if(rand==0) {
							writeMsg(myChan,"Sure is quiet in here");
						}
						else if(rand==1) {
							writeMsg(myChan,"It all returns to nothing");
						}
						else {
							writeMsg(myChan,"We have a bunch of fucking idle-masters in here, "+BOLD+"WAKE THE FUCK UP ASSHOLES");
						}
						idleTime = System.currentTimeMillis();
					}
				}
				else {
					int firstSpace = line.indexOf(" ");
					if(firstSpace!=-1) {
						String channelMessage = line.substring(firstSpace);
						String lchannelMessage = channelMessage.toLowerCase();

						if(lchannelMessage.startsWith(" join :"+myChan.toLowerCase())) {
							//							fork("addRecent"," JOIN",user,ircName);
							addRecent(" JOIN",user,ircName,false);
							fork("updateStats",user,ircName,"channelJoin");
							lastJoin = "*** "+user+" has joined "+myChan;
							if(!shitUser) {
								fork("parseJoin",line,user,ircName);
							}
						}
						else if(lchannelMessage.startsWith(" nick :")) {
							if(userList.containsKey(user.toLowerCase())) {
								System.out.println("READING: "+line);
								//								fork("addRecent"," NICK-"+user, line.substring(line.lastIndexOf(":")+1),ircName);
								addRecent(" NICK-"+user, line.substring(line.lastIndexOf(":")+1),ircName,true);
								if(!shitUser) {
									fork("parseNick",line,user,ircName);
								}
							}
						}
						else if(lchannelMessage.startsWith(" quit :") || lchannelMessage.startsWith(" part "+myChan.toLowerCase())) {
							if(userList.containsKey(user.toLowerCase())) {
								System.out.println("READING: "+line);
								//								fork("addRecent"," QUIT",user,ircName);
								addRecent(" QUIT",user,ircName,false);
								if(!shitUser) {
									fork("parseQuit",line,user,ircName);
								}
							}
						}
						else if(lchannelMessage.startsWith(" privmsg "+myChan.toLowerCase()+" :")) {
							channelMessage = channelMessage.substring((" privmsg "+myChan.toLowerCase()+" :").length());
							lchannelMessage = channelMessage.toLowerCase();

							if(lchannelMessage.startsWith(myNick.toLowerCase()+":")) {
								idleTime = System.currentTimeMillis();
								if(!shitUser) {
									fork("parseEliza",line,user,ircName);
								}
							}
							else if(channelMessage.startsWith("!list") || channelMessage.startsWith("@find") || channelMessage.startsWith("@fserves") || channelMessage.startsWith("@xdcc")) {
								write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
								write("KICK "+myChan+" "+user+" :Fuck off");
							}
							// For trivia bot
							/*
							  else if(channelMessage.indexOf("Naruto: ")!=-1 && user.equalsIgnoreCase("trivia")) {
							  String question = channelMessage.substring(channelMessage.indexOf("Naruto:")).trim();
							  if(triviaBotAnswers.containsKey(question)) {
							  delay(3000,1000);
							  writeMsg(myChan,triviaBotAnswers.get(question));
							  lastTriviaBotQuestion = null;
							  }
							  else {
							  delay(1000,100);
							  writeMsg(myChan,".next");
							  lastTriviaBotQuestion = question;
							  }
							  }
							  else if(channelMessage.indexOf("Bleach: ")!=-1 && user.equalsIgnoreCase("trivia")) {
							  String question = channelMessage.substring(channelMessage.indexOf("Bleach:")).trim();
							  if(triviaBotAnswers.containsKey(question)) {
							  delay(3000,1000);
							  writeMsg(myChan,triviaBotAnswers.get(question));
							  lastTriviaBotQuestion = null;
							  }
							  else {
							  delay(1000,100);
							  writeMsg(myChan,".next");
							  lastTriviaBotQuestion = question;
							  }
							  }
							  else if(channelMessage.indexOf("Pokemon: ")!=-1 && user.equalsIgnoreCase("trivia")) {
							  String question = channelMessage.substring(channelMessage.indexOf("Pokemon:")).trim();
							  if(triviaBotAnswers.containsKey(question)) {
							  delay(3000,1000);
							  writeMsg(myChan,triviaBotAnswers.get(question));
							  lastTriviaBotQuestion = null;
							  }
							  else {
							  lastTriviaBotQuestion = question;
							  System.out.println("Last Question = "+question);
							  delay(1000,100);
							  writeMsg(myChan,".next");
							  }
							  }
							  else if(channelMessage.indexOf("Time's up! The answer was:")!=-1 && lastTriviaBotQuestion!=null) {
							  String answer = channelMessage.substring(channelMessage.indexOf("Time's up! The answer was:")+26).trim();
							  System.out.println("Adding: "+lastTriviaBotQuestion+" = "+answer);
							  triviaBotAnswers.put(lastTriviaBotQuestion,answer);
							  lastTriviaBotQuestion = null;
							  }
							  else if(channelMessage.indexOf("Skipping question. The answer was:")!=-1 && lastTriviaBotQuestion!=null) {
							  String answer = channelMessage.substring(channelMessage.indexOf("Skipping question. The answer was:")+34).trim();
							  System.out.println("Adding: "+lastTriviaBotQuestion+" = "+answer);
							  triviaBotAnswers.put(lastTriviaBotQuestion,answer);
							  lastTriviaBotQuestion = null;
							  }
							*/
							else if(channelMessage.trim().equalsIgnoreCase(".trivia")) {
								write("KICK "+myChan+" "+user+" :HAHAHAHAHA");
								delay(1000,300);
								writeMsg(myChan,".strivia");
							}
							else if(channelMessage.startsWith(".trivia")) {
								try {
									int trivNumber = Integer.parseInt(channelMessage.substring(7).trim());
									if(trivNumber>20 || trivNumber==0) {
										write("KICK "+myChan+" "+user+" :HAHAHAHAHA");
										delay(1000,300);
										writeMsg(myChan,".strivia");
									}
									else if(System.currentTimeMillis()-triviaBotTime > triviaBotTimeout) {
										triviaBotTime = System.currentTimeMillis();
									}
									else {
										write("KICK "+myChan+" "+user+" :No more trivia");
										delay(1000,300);
										writeMsg(myChan,".strivia");
									}
								}
								catch(Exception e) {
									delay(1000,300);
									writeMsg(myChan,".strivia");
								}
							}
							else if(user.equals("Trivia") && channelMessage.startsWith("Round of trivia complete")) {
								triviaBotTime = System.currentTimeMillis();
							}
							else if(lchannelMessage.startsWith(myNick.toLowerCase())) {
								idleTime = System.currentTimeMillis();
								if(lchannelMessage.trim().length()==myNick.length()) {
									delay(500,300);
									writeMsg(myChan,user);
								}
								else if(lchannelMessage.charAt(myNick.length())==',') {
									if(!shitUser) {
										fork("parseEliza",line,user,ircName);
									}
								}
							}
							else if(channelMessage.startsWith("SCRIPT ")) {
								idleTime = System.currentTimeMillis();
								if(!shitUser) {
									fork("parseScript",line,user,ircName);
								}
							}
							else if(channelMessage.startsWith("YOUTUBE")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - youtubeTime > youtubeTimeout) {
									if(!shitUser) {
										fork("parseYoutube",formattedLine,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("ANIME ")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pantsuTime > pantsuTimeout) {
									if(!shitUser && channelMessage.length()>6) {
										fork("parseAnime",channelMessage.substring(5).trim(),user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("IMDB ")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pantsuTime > pantsuTimeout) {
									if(!shitUser && channelMessage.length()>6) {
										fork("parseIMDB",channelMessage.substring(5).trim(),user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("PANTSU")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pantsuTime > pantsuTimeout) {
									if(!shitUser) {
										fork("parsePantsu",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("BOOBIES")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pantsuTime > pantsuTimeout) {
									if(!shitUser) {
										fork("parseBoobies",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("KICK")) {
								idleTime = System.currentTimeMillis();
								if(!shitUser) {
									fork("parseKick",line,user,ircName);
								}
							}
							else if(channelMessage.startsWith("TRIVIA")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - triviaTime > triviaTimeout1) {
									if(!shitUser) {
										fork("parseTrivia",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("GAME")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										fork("parseGame",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("STATS")) {
								idleTime = System.currentTimeMillis();
								if((System.currentTimeMillis() - statsTime) > statsTimeout) {
									if(!shitUser) {
										fork("parseStats",line,user);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("FORTUNE")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - fortuneTime > fortuneTimeout) {
									if(!shitUser) {
										fork("parseFortune",line,user,ircName,null);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("ROLL")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - rollTime > rollTimeout) {
									if(!shitUser) {
										fork("parseRoll",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("8BALL")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - rollTime > rollTimeout) {
									if(!shitUser) {
										fork("parse8Ball",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("MOAR")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pornTime > pornTimeout) {
									if(!shitUser) {
										fork("parseMOAR",line,user);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("LESS")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - pornTime > pornTimeout) {
									if(!shitUser) {
										fork("parseLESS",line,user);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if((channelMessage.startsWith("LOLI ") || channelMessage.trim().startsWith("LOLI")) && channelMessage.substring(4).trim().indexOf(" ")==-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										fork("parseLoli",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("WORTH") && (channelMessage.trim().equals("WORTH") || channelMessage.startsWith("WORTH "))) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										fork("parseWorth",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.trim().equals("BUY")) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										writeMsg(user,"You can /msg me with one of the following things you want to buy: 'trigger', 'voice', 'topic', 'kick', 'kickword', 'cannon', or 'halfops'");
										writeMsg(user,"Append any one of those after the '/msg "+myNick+" BUY' to buy that item using your chan dollars.");
										writeMsg(user,"For 'trigger', 'topic' and 'kickword', append your submission at the end. Example: /msg "+myNick+" BUY trigger snausages");
										writeMsg(user,buyTable.getPriceString());
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("NORIO")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										fork("parseWakamoto",line,user,ircName);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.indexOf("TOPPAGE")!=-1) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - gameTime > gameTimeout) {
									if(!shitUser) {
										writeMsg(myChan,"Gainax Top page Image: "+toppage.lastImage);
									}
								}
								else {
									checkFailedTrigger(user,ircName);
								}
							}
							else if(channelMessage.startsWith("TRANSLATE RSS") ||
									  channelMessage.startsWith("RSS TRANSLATE") ||
									  channelMessage.startsWith("TRANSRSS")) {
								String googleURL = "http://translate.google.com/translate?hl=en&sl=ja&tl=en&u="+HTTPClient.convert(lastRSSURL);
								System.out.println("Google Translate URL: "+googleURL);
								String tinyURL = createTinyURL(googleURL);
								if(tinyURL!=null) {
									writeMsg(myChan,BOLD+"English: "+BOLD+tinyURL);
								}
							}
							// for testing
							else if(channelMessage.startsWith("ECHO ") && user.equalsIgnoreCase("jonlin")) {
								//								writeMsg(myChan,channelMessage.substring(5));
								writeMsg(myChan,formattedLine.substring((":jonlin!jonlin@66.45.116.55 PRIVMSG #egf :ECHO ").length()));
							}
							else if(checkFilterWord(formattedLine)) {
								idleTime = System.currentTimeMillis();
								if(System.currentTimeMillis() - filterTime > filterTimeout) {
									if(!shitUser) {
										fork("parseTrigger",line,user,ircName);
									}
								}
							}
							else if(lchannelMessage.indexOf(" desu")!=-1 ||
									  //									  lchannelMessage.indexOf("weeaboo")!=-1 ||
									  lchannelMessage.indexOf("watashi ")!=-1 ||
									  lchannelMessage.indexOf("gattai")!=-1 ||
									  lchannelMessage.indexOf("sayonara")!=-1 ||
									  lchannelMessage.indexOf("gomen")!=-1 ||
									  lchannelMessage.indexOf("aimasu")!=-1 ||
									  lchannelMessage.indexOf("wasure")!=-1 ||
									  lchannelMessage.indexOf("akuma")!=-1 ||
									  lchannelMessage.indexOf("tamashi")!=-1 ||
									  lchannelMessage.indexOf("kansai")!=-1 ||
									  lchannelMessage.indexOf("kyoudai")!=-1 ||
									  lchannelMessage.indexOf("naruto")!=-1 ||
									  lchannelMessage.indexOf("gaijin")!=-1 ||
									  lchannelMessage.indexOf("nihongo")!=-1 ||
									  lchannelMessage.indexOf("japanese")!=-1 ||
									  lchannelMessage.indexOf("hiragana")!=-1 ||
									  lchannelMessage.indexOf("katakana")!=-1) {

								if(System.currentTimeMillis() - timestamp > 60000) {
									if(System.currentTimeMillis() - pornTime > pornTimeout) {
										timestamp = System.currentTimeMillis();
										if(!shitUser) {
											pornTime = System.currentTimeMillis();
											writeMsg(myChan,makeJapanesePhrase());
										}
									}
								}
							}
							else {
								idleTime = System.currentTimeMillis();
								
								//								System.out.println("user="+user);
								if(!user.equalsIgnoreCase(myNick) && !user.equalsIgnoreCase("Ornette") && !user.equalsIgnoreCase("jonlin") && !user.toLowerCase().equals("brikhaus") && !user.toLowerCase().equals("zapx") && !user.toLowerCase().equals("reichu") && !user.equalsIgnoreCase("ssd") && !user.equalsIgnoreCase("TheEvaMonkey")) {
									for(int x=0;x<kickWords.size();x++) {
										String kickWord = (String)kickWords.elementAt(x);
										if(lchannelMessage.indexOf(kickWord.toLowerCase())!=-1) {
											writeMsg(myChan,"Did someone just say "+BOLD+kickWord+BOLD+"? Cause I think I just heard someone say "+BOLD+kickWord+BOLD);
											write("KICK "+myChan+" "+user+" :Don't take it personally");
											break;
										}
									}
								}
								else {
									if(System.currentTimeMillis() - timestamp > randomTimeout) {
										if(lchannelMessage.indexOf("fuck you fuyu")!=-1 ||
											lchannelMessage.indexOf("fuck you, fuyu")!=-1) {
											delay(1000,300);
											writeMsg(myChan,"no, fuck you");
										}
										else {
											int rand = Math.abs(r.nextInt(30));
											if(user.equalsIgnoreCase("enkiv2")) {
												/*
												if(rand<13) {
													writeMsg(myChan,"I don't think anyone cares...");
												}
												else if(rand>17) {
													writeMsg(myChan,"Nobody wants to know...");
												}
												else {
													writeMsg(myChan,"Time to shut the fuck up...");
												}
												*/
											}
											else if(user.equalsIgnoreCase("naveryw")) {
												/*
												if(rand<13) {
													writeMsg(myChan,"I think someone is full of shit here...");
												}
												else if(rand>17) {
													writeMsg(myChan,"Yeah right, dream on loser.");
												}
												else {
													writeMsg(myChan,"Looks like we have some real idiots here.");
												}
												*/
											}
											else {
												if(rand==0) {
													writeMsg(myChan,"You're retarded.");
												}
												else if(rand==1) {
													writeMsg(myChan,"Do it");
												}
												else if(rand==2) {
													writeMsg(myChan,"Bitch, please");
												}
												else if(rand==3) {
													writeMsg(myChan,"No");
												}
												else if(rand==4) {
													writeMsg(myChan,"Yes");
												}
												else if(rand==5) {
													writeMsg(myChan,"DAMN it feels good to be a gansta.");
												}
												else if(rand==6) {
													writeMsg(myChan,"Maybe");
												}
												else if(rand==7) {
													writeMsg(myChan,"You're wrong.");
												}
												else if(rand==8) {
													writeMsg(myChan,"Whatever");
												}
												else if(rand==9) {
													writeMsg(myChan,"Right on, sucka");
												}
												else if(rand==10) {
													writeMsg(myChan,"What?!");
												}
												else if(rand==11) {
													write("TOPIC "+myChan+" :"+topic);
												}
												else if(rand==12) {
													writeMsg(myChan,randomMessage);
												}
												else if(rand<22) {
													//						else {
													fork("parseFortune",line,user,"random");
												}
											}
										}
									}
									timestamp = System.currentTimeMillis();
								}

								if(System.currentTimeMillis() - trendTime > trendTimeout) {
									if(System.currentTimeMillis() - timestamp>60000) {
										timestamp = System.currentTimeMillis();
										fork("googleTrends",user);
										trendTime = System.currentTimeMillis();
									}
								}
							}
						}
						else if(lchannelMessage.startsWith(" privmsg #tokyotosho-api :")) {
							String toshoLine = formattedLine;

							if(user.equals("TokyoTosho")) {
								if(toshoLine.indexOf("Torrent"+((char)30))!=-1) {
									int index = toshoLine.indexOf(((char)30));
									toshoLine = toshoLine.substring(index+1);
									index = toshoLine.indexOf(((char)30));
									if(index!=-1) {
										// torrent ID
										toshoLine = toshoLine.substring(index+1);
										index = toshoLine.indexOf(((char)30));
										if(index!=-1) {
											// type
											String type = toshoLine.substring(0,index);
											//											System.out.println("Type: "+type);
											if(type.equalsIgnoreCase("anime") || type.equalsIgnoreCase("batch")) {
												toshoLine = toshoLine.substring(index+1);
												index = toshoLine.indexOf(((char)30));
												if(index!=-1) {
													// type ID
													toshoLine = toshoLine.substring(index+1);
													index = toshoLine.indexOf(((char)30));
													if(index!=-1) {
														// release name
														release = toshoLine.substring(0,index);
														//														System.out.println("Release: "+release);
														toshoLine = toshoLine.substring(index+1);
														index = toshoLine.indexOf(((char)30));
														if(index!=-1) {
															// torrent URL
															torrent = toshoLine.substring(0,index);
															//															System.out.println("URL: "+torrent);
															fork("processRSS",release,torrent);
															release = null;
															torrent = null;
														}
													}
												}
											}
										}
									}
								}
								else {
									channelMessage = channelMessage.substring((" privmsg #tokyotosho-api :").length());
									if(channelMessage.startsWith("Release: Anime]")) {
										release = channelMessage.substring(16).trim();
									}
									else if(channelMessage.startsWith("Torrent: ")) {
										torrent = channelMessage.substring(8).trim();
									}
									else if(channelMessage.startsWith("Size: ")) {
										if(release!=null && torrent!=null) {
											fork("processRSS",release,torrent);
										}
										release = null;
										torrent = null;
									}
								}
							}
						}
					}
				}
				if(nickChangeKick && ((System.currentTimeMillis() - lastNickChange) > analNickTimeout)) {
					nickChangeKick = false;
					System.out.println("----ANAL NICK CHANGE: OFF");
				}
				if(idleStats.containsKey(user.toLowerCase())) {
					idleStats.remove(user.toLowerCase());
				}
			}
			catch(Exception e) {e.printStackTrace();}
		}
	}



	public void fork(String method)
	{
		fork(method,"","","","");
	}

	public void fork(String method, String param1)
	{
		fork(method,param1,"","","");
	}

	public void fork(String method, String param1, String param2)
	{
		fork(method,param1,param2,"","");
	}

	public void fork(String method, String param1, String param2, String param3)
	{
		fork(method,param1,param2,param3,"");
	}

	public void fork(String method, String param1, String param2, String param3, String param4)
	{
		String[] params = new String[4];
		params[0]=param1;
		params[1]=param2;
		params[2]=param3;
		params[3]=param4;
		if(!pool.getThread(new WorkKMethod(method,params))) {
			System.out.println("OOOOOOHHHHHHHHHHHHHHHHHHHHHHH SHIT!!!!!!!!!! Can't do work: "+method);
		}
	}


	// For unicode support


	public String readLine(InputStream in) throws IOException
	{
		StringBuffer rval = null;
		//		boolean isNonAscii = false;

		byte[] buffer = new byte[1024];
		int count = 0;
		int i = in.read();
		if(i!=-1) {
			rval = new StringBuffer();
			while(i!=-1 && (char)i != '\n') {
				//				if(i>127) {
				//					isNonAscii = true;
				//				}
				buffer[count] = (byte)i;

				i = in.read();
				count++;

				if(count>=1024) {
					rval.append(new String(buffer,"UTF-8"));
					count = 0;
				}
			}
			if(count>0) {
				rval.append(new String(buffer,0,count,"UTF-8"));
			}
		}

		//		if(isNonAscii) {
		//			System.out.println("NONASCII CHARSET: "+rval);
		//		}

		if(rval!=null) {
			return(rval.toString().trim());
		}
		else {
			return(null);
		}

		/*
		  StringBuffer rval = new StringBuffer();
		  byte[] buffer = new byte[30];
		  int bytesRead = in.read(buffer);
		  boolean bailout = false;
		  boolean bailoutLF = false;
		  while(bytesRead>0) {
		  for(int x=0;x<bytesRead;x++) {
		  if(((char)buffer[x]) == '\r') {
		  rval.append(new String(buffer,0,x,"ISO-8859-1"));
		  bailoutLF = true;
		  }
		  else if(((char)buffer[x]) == '\n') {
		  if(!bailoutLF) {
		  rval.append(new String(buffer,0,x-1,"ISO-8859-1"));
		  }
		  bailout = true;
		  }
		  else if(bailout || bailoutLF) {
		  in.unread(buffer,x,bytesRead-x);
		  bailout = true;
		  break;
		  }
		  }
		  if(bailout) {
		  break;
		  }
		  else {
		  bytesRead = in.read(buffer);
		  }
		  }

		  return(rval.toString());
		*/


		/*
		  String rval = null;

		  synchronized (ircSync) {
		  byte[] buffer = new byte[4096];
		  int bytesread = in.read(buffer);
		  int index = 0;
		  if(bytesread>0) {
		  for(index=0;index<bytesread;index++) {
		  if((char)buffer[index] == '\n') {
		  rval = new String(buffer,0,index,"UTF-8");
		  rval = rval.trim();
		  in.unread(buffer,index+1,bytesread-(index+1));
		  break;
		  }
		  }
		  }
		  }

		  return(rval);
		*/
	}


	public void printThreads()
	{
		Map liveThreads = Thread.getAllStackTraces();
		for (Iterator i = liveThreads.keySet().iterator(); i.hasNext(); ) {
			Thread key = (Thread)i.next();
			System.err.println("\nThread " + key.getName());
			StackTraceElement[] trace = (StackTraceElement[])liveThreads.get(key);
			for (int j = 0; j < trace.length; j++) {
				System.err.println("\tat " + trace[j]);
			}
		}
	
		/*
		  ThreadGroup top = null;
		  Thread current = Thread.currentThread();
		  top = current.getThreadGroup();
		  while(top.getParent()!=null) {
		  top = top.getParent();
		  }
			
		  System.out.println("\n\nTHREADS: #########################################################################");
		  //		error("THREADS: #########################################################################");
			
		  Thread[] threads = new Thread[10000];
		  int number = top.enumerate(threads);
		  for(int x = 0;x<number;x++) {
		  System.out.println("thread("+threads[x].getName()+") group("+threads[x].getThreadGroup().getName()+") priority("+threads[x].getPriority()+")");
		  //			error("thread("+threads[x].getName()+") group("+threads[x].getThreadGroup().getName()+") priority("+threads[x].getPriority()+")");
		  System.out.println("      isAlive("+threads[x].isAlive()+") isInterrupted("+threads[x].isInterrupted()+") isDaemon("+threads[x].isDaemon()+")");
		  //			error("      isAlive("+threads[x].isAlive()+") isInterrupted("+threads[x].isInterrupted()+") isDaemon("+threads[x].isDaemon()+")");
		  threads[x].dumpStack();
		  System.out.println("\n");
		  }
		*/
	}

	public boolean checkFilterWord(String in) 
	{
		boolean rval = false;

		int index = in.indexOf(" PRIVMSG "+myChan+" :");
		in = in.substring(index + (" PRIVMSG "+myChan+" :").length());

		//		System.out.println("checking: "+in+" against "+filterWord);
		try {
			byte[] line = in.getBytes("UTF-8");
			byte[] fword = filterWord.getBytes("UTF-8");

			if(line.length>=fword.length) {
				for(int x=0;x<line.length;x++) {
					boolean allMatch = true;
					for(int y=0;y<fword.length;y++) {
						if(fword[y]!=line[x+y]) {
							allMatch = false;
							break;
						}
					}
					if(allMatch) {
						rval = true;
						break;
					}
				}
			}
		}
		catch(Exception e) {
			//			e.printStackTrace();
		}

		return(rval);
	}



	public boolean parseCTCP(String in, String user)
	{
		boolean rval = false;
		int index = in.indexOf(":");
		//		System.out.println("PARSE CTCP: "+in);
		if(index==0) {
			index = in.indexOf(":",1);
			if(index!=-1) {
				char delim = (char)1;
				String ctcpMessage = in.substring(index+1);
				if(ctcpMessage.charAt(0)==(delim)) {
					ctcpMessage = ctcpMessage.substring(1);
					index = ctcpMessage.indexOf(delim);
					if(index!=-1) {
						ctcpMessage = ctcpMessage.substring(0,index);
					}
					System.out.println("CTCP: "+ctcpMessage);
					if(ctcpMessage.startsWith("FINGER")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"Kozo Fuyutsuki, Gendo's right hand man"+delim);
					}
					else if(ctcpMessage.startsWith("VERSION")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"Fuyutsuki:2.0:Java"+delim);
					}
					else if(ctcpMessage.startsWith("SOURCE")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"::"+delim);
					}
					else if(ctcpMessage.startsWith("USERINFO")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"Snausages"+delim);
					}
					else if(ctcpMessage.startsWith("CLIENTINFO")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"Snausages"+delim);
					}
					else if(ctcpMessage.startsWith("ERRMSG")) {
						delay(500,100);
						write("NOTICE "+user+" :"+delim+"ERRMSG:"+ctcpMessage.substring(7)+":No error"+delim);
					}
					else if(ctcpMessage.startsWith("PING")) {
						write("NOTICE "+user+" :"+delim+ctcpMessage+delim);
					}
					else if(ctcpMessage.startsWith("TIME")) {
						write("NOTICE "+user+" :"+delim+(new Date())+delim);
					}
					else {
						write("NOTICE "+user+" :"+delim+"ERRMSG:"+ctcpMessage+":Huh?"+delim);
					}
					rval = true;
				}
			}
		}
		return(rval);
	}

	public String removeFormatting(String in) throws java.io.UnsupportedEncodingException
	{
		// 32 58 106 111 110 108 105 110 95 99 119 2 58 2 32 116 101 115 116
		//' ' :  j   o   n   l   i   n   _  c  w   ? :  ?' ' t   e   s   t

		//:Rane-!R@Rizon-9F11DACF.elisa-laajakaista.fi PRIVMSG #egf :Dunno 4about underlined.
		// 32 58 2 68 117 110 110 111 2 32 3 52 97 98 111 117 116 3 32 117 110 100 101 114 108 105 110 101 100 46
		//' ' :  ? D  u   n   n   o   ? '' ? 4  a  b  o   u   t   ?' ' 

		String out = in;
		//		System.out.println(encode(in));
		boolean colorFormat = false;
		byte[] inBytes = in.getBytes("UTF-8");
		byte[] outBytes = new byte[4096];
		int index = 0;
		for(int x=0;x<inBytes.length;x++) {
			int i = (int)inBytes[x];
			if(!(i<10 || (i>10 && i<13) || (i>13 && i<32))) {
				outBytes[index++] = inBytes[x];
			}
			else if(i==3) {
				if(!colorFormat) {
					x++;
					if(x==inBytes.length) {
						break;
					}
					i = (int)inBytes[x];
					if((i>=48 && i<=57) || (i>=65 && i<=70)) {
						colorFormat = true;
						if(i==49) {
							x++;
							if(x==inBytes.length) {
								break;
							}
							if(!(i>=48 && i<=53)) {
								x--;
							}	
						}		
					}
					else {
						colorFormat = false;
						x--;
					}
				}
				else {
					x++;
					if(x==inBytes.length) {
						break;
					}
					i=(int)inBytes[x];
					if((i>=48 && i<=57) || (i>=65 && i<=70)) {
						if(i==49) {
							x++;
							if(x==inBytes.length) {
								break;
							}
							if(!(i>=48 && i<=53)) {
								x--;
							}				
						}		
					}
					else {
						colorFormat = false;
						x--;
					}
				}
			}
		}
		out = new String(outBytes,0,index,"UTF-8");
		
		/*
		  StringBuffer out = new StringBuffer();
		  boolean colorFormat = false;
		  for(int x=0;x<in.length();x++) {
		  char c = in.charAt(x);
		  int i = ((int)c);
		  if(!(i<10 || (i>10 && i<13) || (i>13 && i<32))) {
		  out.append(c);
		  }
		  else if(i>127) {
		  System.out.print("+");
		  out.append(c);
		  }
		  else if(i==3) {
		  if(!colorFormat) {
		  x++;
		  if(x==in.length()) {
		  break;
		  }
		  i=((int)in.charAt(x));
		  if((i>=48 && i<=57) || (i>=65 && i<=70)) {
		  colorFormat = true;
		  if(i==49) {
		  x++;
		  if(x==in.length()) {
		  break;
		  }
		  if(!(i>=48 && i<=53)) {
		  x--;
		  }				
		  }		
		  }
		  else {
		  colorFormat = false;
		  x--;
		  }
		  }
		  else {
		  x++;
		  if(x==in.length()) {
		  break;
		  }
		  i=((int)in.charAt(x));
		  if((i>=48 && i<=57) || (i>=65 && i<=70)) {
		  if(i==49) {
		  x++;
		  if(x==in.length()) {
		  break;
		  }
		  if(!(i>=48 && i<=53)) {
		  x--;
		  }				
		  }		
		  }
		  else {
		  colorFormat = false;
		  x--;
		  }
		  }
		  }
		  }
		*/
		return(out);
	}


	public void admin(String line, String user)
	{
		try {
			if(line.startsWith("REMOVENICK")) {
				line = line.substring(10).trim();
				String oldnick = (String)nickTranslation.remove(line);
				System.out.println("Removing from nickTranslation: "+line+"="+oldnick);
				writeMsg(user,"Removing from nickTranslation: "+line+" = "+oldnick);
			}
			else if(line.startsWith("ADDNICK")) {
				line = line.substring(7).trim();
				int index = line.indexOf(" ");
				String nick = line.substring(index+1).trim();
				line = line.substring(0,index).trim();
				System.out.println("Adding to nickTranslation: "+line+"="+nick);
				writeMsg(user,"Adding to nickTranslation: "+line+" = "+nick);
				nickTranslation.put(line,nick);
			}
			else if(line.startsWith("CLAIMNICK")) {
				claimNick();
			}	
			else if(line.startsWith("LIST")) {
				for(int x=0;x<10;x++) {
					delay(500,200);
					writeMsg(user,""+filterVector.elementAt(x));
				}
			}
			else if(line.startsWith("REPLACE")) {
				line = line.substring(7).trim();
				int index = Integer.parseInt(line.substring(0,line.indexOf(" ")).trim());
				line = line.substring(line.indexOf(" ")).trim();
				writeMsg(user,"Replacing '"+filterVector.elementAt(index)+"' with '"+line+"'");
				synchronized(this) {
					filterVector.addElement(line);
					filterVector.removeElementAt(index);
				}
			}
			else if(line.startsWith("RELOADSETTINGS")) {
				elizaVector = new Vector();
				loadSettings();
				writeMsg(user,"Settings reloaded");
			}
			else if(line.startsWith("WORD")) {
				if(line.substring(4).trim().length()>0) {
					filterWord = line.substring(4).trim();
				}
				writeMsg(user,"filterWord="+filterWord);
			}
			else if(line.startsWith("TIMEOUT")) {
				if(joinTosho && !joinedTosho) {
					write("JOIN #tokyotosho-api");
					joinedTosho = true;
				}
				line = line.substring(7).trim();
				if(line.startsWith("random")) {
					line = line.substring(6).trim();
					randomTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"random fortune timeout set to: "+line);
				}
				else if(line.startsWith("fortune")) {
					line = line.substring(7).trim();
					fortuneTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"fortune timeout set to: "+line);
				}
				else if(line.startsWith("game")) {
					line = line.substring(4).trim();
					gameTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"game timeout set to: "+line);
				}
				else if(line.startsWith("trivia1")) {
					line = line.substring(7).trim();
					triviaTimeout1 = (long)Integer.parseInt(line);
					writeMsg(user,"Trivia Question timeout set to: "+line);
				}
				else if(line.startsWith("trivia2")) {
					line = line.substring(7).trim();
					triviaTimeout2 = (long)Integer.parseInt(line);
					writeMsg(user,"Trivia Answer timeout set to: "+line);
				}
				else if(line.startsWith("filter")) {
					line = line.substring(6).trim();
					filterTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Trigger word timeout set to: "+line);
				}
				else if(line.startsWith("stats")) {
					line = line.substring(5).trim();
					statsTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Stats timeout set to: "+line);
				}
				else if(line.startsWith("idle")) {
					line = line.substring(5).trim();
					idleTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Idle timeout set to: "+line);
				}
				else if(line.startsWith("flood")) {
					line = line.substring(5).trim();
					floodTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Flood timeout set to: "+line);
				}
				else if(line.startsWith("roll")) {
					line = line.substring(4).trim();
					rollTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Roll timeout set to: "+line);
				}
				else if(line.startsWith("voice")) {
					line = line.substring(5).trim();
					voiceTimeout = Integer.parseInt(line);
					writeMsg(user,"Voice timeout set to: "+line);
				}
				else if(line.startsWith("porn")) {
					line = line.substring(4).trim();
					pornTimeout = (long)Integer.parseInt(line);
					writeMsg(user,"Porn timeout set to: "+line);
				}
				else if(line.startsWith("joinspam")) {
					line = line.substring(8).trim();
					joinSpamTimeout = Integer.parseInt(line);
					writeMsg(user,"Join Spam timeout set to: "+line);
				}
				else if(line.startsWith("join")) {
					line = line.substring(4).trim();
					autojoinTimeout = Integer.parseInt(line);
					writeMsg(user,"AutoJoin timeout set to: "+line);
				}
				else if(line.startsWith("analnickon")) {
					line = line.substring(10).trim();
					if(line.length()>0) {
						nickTimeout = Integer.parseInt(line);
						writeMsg(user,"Anal Nick Mode ON set to: "+line);
					}
				}
				else if(line.startsWith("analnickoff")) {
					line = line.substring(11).trim();
					if(line.length()>0) {
						analNickTimeout = Integer.parseInt(line);
						writeMsg(user,"Anal Nick Mode OFF set to: "+line);
					}
				}
				else if(line.startsWith("youtube")) {
					line = line.substring(7).trim();
					if(line.length()>0) {
						youtubeTimeout = Integer.parseInt(line);
						writeMsg(user,"Youtube timeout set to: "+line);
					}
				}
				else if(line.startsWith("pantsu")) {
					line = line.substring(7).trim();
					if(line.length()>0) {
						pantsuTimeout = Integer.parseInt(line);
						writeMsg(user,"Pantsu timeout set to: "+line);
					}
				}
				else if(line.startsWith("loli")) {
					line = line.substring(4).trim();
					if(line.length()>0) {
						loliTimeout = Integer.parseInt(line);
						writeMsg(user,"Loli timeout set to: "+line);
					}
				}
				else if(line.startsWith("google")) {
					line = line.substring(6).trim();
					if(line.length()>0) {
						trendTimeout = Integer.parseInt(line);
						writeMsg(user,"Google Trends timeout set to: "+line);
					}
				}
				else if(line.startsWith("rps")) {
					line = line.substring(3).trim();
					if(line.length()>0) {
						rpsTimeout = Integer.parseInt(line);
						writeMsg(user,"RPS timeout set to: "+line);
					}
				}
				else if(line.startsWith("failed")) {
					line = line.substring(6).trim();
					if(line.length()>0) {
						failedTriggerTimeout = Integer.parseInt(line);
						writeMsg(user,"Failed Trigger timeout set to: "+line);
					}
				}
				else {
					writeMsg(user,"Timeouts: random="+randomTimeout+" fortune="+fortuneTimeout+" game="+gameTimeout+" trivia1="+triviaTimeout1+" trivial2="+triviaTimeout2+" filter="+filterTimeout+" stats="+statsTimeout+" idle="+idleTimeout+" flood="+floodTimeout+" roll="+rollTimeout+" voice="+voiceTimeout+" porn="+pornTimeout+" youtube="+youtubeTimeout+" pantsu="+pantsuTimeout+" join="+autojoinTimeout+" joinspam="+joinSpamTimeout+" analnickon="+nickTimeout+" analnickoff="+analNickTimeout+" loli="+loliTimeout+" google="+trendTimeout+" rps="+rpsTimeout+" failed="+failedTriggerTimeout);
				}
				saveSettings();
			}
			else if(line.startsWith("RELOADTRIVIA")) {
				loadTrivia();
				writeMsg(user,"Trivia questions reloaded");
			}
			else if(line.startsWith("ADMIN")) {
				line = line.substring(5).trim();
				writeMsg(user,"Setting admin user to: "+line);
				adminUser = line;
			}
			else if(line.startsWith("NICKKICK")) {
				if(nickChangeKick) {
					writeMsg(user,"Nick Change Kicking is turned off");
					nickChangeKick = false;
				}
				else {
					writeMsg(user,"Nick Change Kicking is turned on");
					nickChangeKick = true;
					lastNickChange = System.currentTimeMillis();
				}
			}
			else if(line.startsWith("RESETLOLIMESSAGE")) {
				writeMsg(user,"Loli Message reset");
				loliMessage = null;
				saveStats();
			}
			else if(line.startsWith("RESETKICKS")) {
				kickAttempts = new Vector();
				writeMsg(user,"Kick vector cleared");
			}
			else if(line.startsWith("NICK")) {
				line = line.substring(4).trim();
				writeMsg(user,"Changing my nick to: "+line);
				myNick = line;
				write("NICK "+myNick);
			}
			else if(line.startsWith("SHITADD")) {
				line = line.substring(7).trim();
				if(line.length()>0) {
					writeMsg(user,"Adding to ShitList: "+line);
					if(!shitList.contains(line.toLowerCase())) {
						shitList.addElement(line.toLowerCase());
						saveSettings();
					}
				}
				else {
					StringBuffer list = new StringBuffer();
					for(int x=0;x<shitList.size();x++) {
						list.append(shitList.elementAt(x)+" ");
					}
					writeMsg(user,"ShitList = "+list);
				}
			}
			else if(line.startsWith("SHITREMOVE")) {
				line = line.substring(10).trim();
				if(line.length()>0) {
					writeMsg(user,"Removing from ShitList: "+line);
					shitList.removeElement(line);
					saveSettings();
				}
				else {
					StringBuffer list = new StringBuffer();
					for(int x=0;x<shitList.size();x++) {
						list.append(shitList.elementAt(x)+" ");
					}
					writeMsg(user,"ShitList = "+list);
				}
			}
			else if(line.startsWith("SAVESTAT")) {
				if(saveStats()) {
					writeMsg(user,"Statistics saved");
				}
				else {
					writeMsg(user,"Statistics could not be saved");
				}
				saveSettings();
			}
			else if(line.startsWith("SHUTDOWN")) {
				writeMsg(myChan,"later fuckos");
				saveStats();
				saveSettings();
				write("QUIT :So long, jerkwads");
				System.exit(0);
			}
			else if(line.startsWith("GETSTAT")) {
				printStats(line.substring(7).trim(),user);
			}
			else if(line.startsWith("SAVETRIGGERS")) {
				saveFilters();
				writeMsg(user,"Triggers saved");
			}
			else if(line.startsWith("COMBINE")) {
				line = line.substring(7).trim().toLowerCase();
				int index = line.indexOf(" ");
				String user1 = line.substring(0,index).trim();
				String user2 = line.substring(index).trim();
				combineStats(line,user1,user2);
				writeMsg(user,"User '"+user1+"' merged into "+user2+" and the former has been removed from stats");
				saveStats();
				saveFilters();
			}
			else if(line.startsWith("REMOVE")) {
				line = line.substring(6).trim().toLowerCase();
				scriptStats.remove(line);
				triggerStats.remove(line);
				responsesStats.remove(line);
				fortuneStats.remove(line);
				gameStats.remove(line);
				triviaStats.remove(line);
				answerStats.remove(line);
				rollStats.remove(line);
				msgStats.remove(line);
				youtubeStats.remove(line);
				pantsuStats.remove(line);
				channelStats.remove(line);
				channelCapsStats.remove(line);
				channelLinksStats.remove(line);
				channelShortStats.remove(line);
				channelLongStats.remove(line);
				channelQuestionStats.remove(line);
				channelJoinStats.remove(line);
				channelEmoticonStats.remove(line);
				rpsStats.remove(line);
				writeMsg(user,"User '"+line+"' removed from stats");
				saveStats();
				saveFilters();
			}
			else if(line.startsWith("KICKWORDCLEAR")) {
				kickWords = new Vector();
				addedKickWords = new Vector();
				writeMsg(user,"Cleared Kick Words");
			}
			else if(line.startsWith("KICKWORD")) {
				line = line.substring(8).trim().toLowerCase();
				if(line.length()>0) {
					kickWords.addElement(line);
					writeMsg(user,"'"+line+"' added to kick words");
				}
				else {
					StringBuffer tmp = new StringBuffer();
					for(int z=0;z<kickWords.size();z++) {
						tmp.append(kickWords.elementAt(z).toString());
						tmp.append(", ");
					}
					writeMsg(user,"Kick Words="+tmp);
				}
			}
			else if(line.startsWith("KICKBAN")) {
				line = line.substring(7).trim();
				int index = line.indexOf(" ");
				write("KICK "+myChan+" "+line.substring(0,index).trim()+" :Later sucker");
				write("MODE "+myChan+" +b "+line.substring(index).trim());
			}				
			else if(line.startsWith("KICK")) {
				line = line.substring(4).trim();
				write("KICK "+myChan+" "+line+" :"+line);
			}
			else if(line.startsWith("BAN")) {
				line = line.substring(3).trim();
				write("MODE "+myChan+" +b "+line);
			}
			else if(line.startsWith("FLOODLINES")) {
				line = line.substring(10).trim();
				if(line.length()>0) {
					floodLines = Integer.parseInt(line);
					writeMsg(user,"Flood lines set to '"+line+"'");
				}
				else {
					writeMsg(user,"Flood lines='"+floodLines+"'");
				}
				saveSettings();
			}
			else if(line.startsWith("HFLOODLINES")) {
				line = line.substring(11).trim();
				if(line.length()>0) {
					hFloodLines = Integer.parseInt(line);
					writeMsg(user,"Horizontal Flood lines set to '"+line+"'");
				}
				else {
					writeMsg(user,"Horizontal Flood lines='"+hFloodLines+"'");
				}
				saveSettings();
			}
			else if(line.startsWith("FLOODLENGTH")) {
				line = line.substring(11).trim();
				if(line.length()>0) {
					hFloodLength = Integer.parseInt(line);
					writeMsg(user,"Horizontal flood length set to '"+line+"'");
				}
				else {
					writeMsg(user,"Horizontal flood length='"+hFloodLength+"'");
				}
				saveSettings();
			}
			else if(line.startsWith("FLOODMULT")) {
				line = line.substring(9).trim();
				if(line.length()>0) {
					floodMultiplier = Integer.parseInt(line);
					writeMsg(user,"Flood multiplier set to '"+line+"'");
				}
				else {
					writeMsg(user,"Flood multiplier='"+floodMultiplier+"'");
				}
				saveSettings();
			}
			else if(line.startsWith("GREET")) {
				line = line.substring(5).trim();
				if(line.length()>0) {
					greetingProbability = Integer.parseInt(line);
					writeMsg(user,"Greeting Probaility set to '"+line+"'");
				}
				else {
					writeMsg(user,"Greeting Probability='"+greetingProbability+"'");
				}
				saveSettings();
			}
			else if(line.startsWith("LOLIBASE")) {
				line = line.substring(8).trim();
				if(line.length()>0) {
					loliKillBase = Integer.parseInt(line);
					writeMsg(user,"Loli Kill Base set to '"+line+"'");
				}
				else {
					writeMsg(user,"Loli Kill Base='"+loliKillBase+"'");
				}
			}
			else if(line.startsWith("LOLIROLL")) {
				line = line.substring(8).trim();
				if(line.length()>0) {
					loliKillRoll = Integer.parseInt(line);
					writeMsg(user,"Loli Kill Roll set to '"+line+"'");
				}
				else {
					writeMsg(user,"Loli Kill Roll='"+loliKillRoll+"'");
				}
			}
			else if(line.startsWith("TOPIC")) {
				line = line.substring(5).trim();
				if(line.length()>0) {
					write("TOPIC "+myChan+" :"+line);
					topic = line;
				}
				else {
					write("TOPIC "+myChan+" :"+topic);
				}
			}
			else if(line.startsWith("THREADS")) {
				printThreads();
			}
			/*
			  else if(line.startsWith("PASTES")) {
			  StringBuffer tmpBuffer = new StringBuffer();
			  for(int x=0;x<pastes.size();x++) {
			  Pair p = (Pair)pastes.elementAt(x);
			  tmpBuffer.append(p.first()+", ");
			  if(x%5==0) {
			  writeMsg(user,tmpBuffer);
			  tmpBuffer = new StringBuffer();
			  delay(1500,500);
			  }
			  }
			  if(tmpBuffer.length()>0) {
			  writeMsg(user,tmpBuffer);
			  }
			  }
			*/
			else if(line.toLowerCase().startsWith("whois")) {
				line = line.substring(5).trim();
				if(userList.containsKey(line.toLowerCase())) {
					String ircName2 = (String)userList.get(line.toLowerCase());
					if(!isJavaClient(ircName2)) {
						if(nickTranslation.containsKey(ircName2)) {
							String realName = (String)nickTranslation.get(ircName2);
							writeMsg(user,UNDERLINE+line+UNDERLINE+" is really "+BOLD+realName+BOLD);
						}
						else {
							writeMsg(user,"I don't seem to know who "+UNDERLINE+line+UNDERLINE+" is supported to be. I've been foiled.");
						}
					}
					else {
						writeMsg(user,"This user is using a client that doesn't identify himself, therefore I cannot determine his identity.");
					}
				}
				else {
					writeMsg(user,"I don't seem to know who "+UNDERLINE+line+UNDERLINE+" is. Then again, I'm retarded.");
				}
			}
			else {
				writeMsg(user,"LIST REPLACE WORD TIMEOUT (random|fortune|game|trivia1|trivia2|roll|filter|stats|idle|flood) RELOADTRIVIA ADMIN NICK SAVESTAT GETSTAT SAVETRIGGERS SHUTDOWN REMOVE KICK BAN KICKBAN FLOODLINES HFLOODLINES FLOODLENGTH FLOODMULT SHITADD SHITREMOVE REMOVENICK ADDNICK LISTNICK GREET TOPIC RESETKICKS RESETLOLIMESSAGE RELOADSETTINGS LOLIBASE LOLIROLL CLAIMNICK");
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void addRecent(String user, String line, String ircName, boolean checkFlood)
	{
		long stamp = System.currentTimeMillis();
		synchronized(recent) {
			String tmp = line;
			if(line.toLowerCase().indexOf("privmsg "+myChan.toLowerCase())!=-1) {
				tmp = line.substring(line.toLowerCase().indexOf(" privmsg "+myChan.toLowerCase()+" :")+(" PRIVMSG "+myChan+" :").length());
			}				
			recent.addElement(new Pair(new Pair(user, ""+stamp),tmp));
			if(recent.size()>recentSize) {
				recent.removeElementAt(0);
			}

			Vector searches = new Vector();
			for(int x=0;x<recent.size();x++) {
				String search = (String)((Pair)recent.elementAt(x)).second();
				String userField = (String)((Pair)((Pair)recent.elementAt(x)).first()).first();
				if(!userField.equals(" JOIN") && !userField.equals(" QUIT") && !userField.startsWith(" NICK-")) {
					if(!searches.contains(search) && 
						search.indexOf("FORTUNE")==-1 &&
						search.indexOf("ROLL")==-1 &&
						search.indexOf("8BALL")==-1 &&
						search.indexOf("TRIVIA")==-1 &&
						search.indexOf("GAME")==-1 &&
						search.indexOf("PANTSU")==-1 &&
						search.indexOf("BOOBIES")==-1 &&
						search.indexOf("YOUTUBE")==-1 &&
						search.indexOf("MOAR")==-1 &&
						search.indexOf("LESS")==-1 &&
						search.indexOf("LOLI")==-1 &&
						search.indexOf("WORTH")==-1 &&
						search.indexOf("NORIO")==-1 &&
						!search.startsWith(".trivia ") &&
						!search.startsWith(".next ") &&
						search.indexOf("KICK")==-1) {
						searches.addElement(search);
						int count = 0;
						for(int z=x;z<recent.size();z++) {
							if(((String)((Pair)recent.elementAt(z)).second()).equalsIgnoreCase(search)) {
								count++;
								if(count>4) {
									if(!kickWords.contains(search)) {
										if(!(((Pair)((Pair)recent.elementAt(z)).first()).first()).toString().equalsIgnoreCase("trivia") && !(((Pair)((Pair)recent.elementAt(z)).first()).first()).toString().equalsIgnoreCase("ornette") && !(((Pair)((Pair)recent.elementAt(z)).first()).first()).toString().equalsIgnoreCase(myNick)) {
											if(search.equals("lol") || search.length()>4) {
												if(r.nextInt(3)==0) {
													if(!userList.contains(search.toLowerCase())) {
														System.out.println("KICKWORD ADDED: "+search);
														kickWords.addElement(search);
														addedKickWords.addElement(new Pair(search,System.currentTimeMillis()+""));
													}
												}
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}
		}

		Vector kickWordsRemove = new Vector();
		for(int x=0;x<addedKickWords.size();x++) {
			Pair p = (Pair)addedKickWords.elementAt(x);
			String search = (String)p.first();
			long time = Long.valueOf((String)p.second()).longValue();
			if(System.currentTimeMillis()-time > 1200000) {
				kickWordsRemove.addElement(p);
				kickWords.remove(search);
			}
		}

		for(int x=0;x<kickWordsRemove.size();x++) {
			addedKickWords.remove(kickWordsRemove.elementAt(x));
		}

		int userCount = 0;
		boolean longLines = false;
		long last = 0;
		int start = 0;
		int end = 10;
		if(!user.equalsIgnoreCase(myNick) && !user.equalsIgnoreCase("Trivia") && user.trim().length()>0 && checkFlood) {
			synchronized(recent) {
				if(recent.size()>floodLines+10) {
					String tmp = "";
					if(line.toLowerCase().indexOf("privmsg "+myChan.toLowerCase())!=-1) {
						tmp = line.substring(line.toLowerCase().indexOf(" privmsg "+myChan.toLowerCase()+" :")+(" PRIVMSG "+myChan+" :").length());
					}				

					int count = 0;
					if(tmp.toUpperCase().equals(tmp) && !tmp.toLowerCase().equals(tmp) && tmp.length()>7) {
						System.out.println("Checking caps");
						for(int z=recent.size()-1;z>=recent.size()-11;z--) {
							String caps = (String)((Pair)recent.elementAt(z)).second();
							if(caps.toUpperCase().equals(caps) && !caps.toLowerCase().equals(caps) && caps.length()>7) {
								count++;
								if(count>2) {
									if(r.nextInt(3)==0) {
										write("KICK "+myChan+" "+user+" :capslock is cruise control for KICK");
										break;
									}
									else {
										System.out.println("CAPSLOCK: not kicking");
									}
								}
							}
						}
					}

					boolean nearFlood = false;
					end = recent.size()-1;
					start = end-(floodLines+10);
					for(int x=end;x>start;x--) {
						Pair p = (Pair)recent.elementAt(x);
						Pair p2 = (Pair)p.first();
						if(((String)p2.first()).equalsIgnoreCase(user)) {
							last = Long.valueOf((String)p2.second()).longValue();
							userCount++;
							if(userCount == floodLines-1) {
								nearFlood = true;
								//								System.out.println("NearFlood = true");
							}
							if(userCount >= floodLines) {
								break;
							}
						}
					}
					//				System.out.println("stamp-last = "+(stamp-last)+" userCount="+userCount);
					end = recent.size()-1;
					start = end-hFloodLines;
					for(int x=end;x>start;x--) {
						Pair p = (Pair)recent.elementAt(x);
						Pair p2 = (Pair)p.first();
						if(!((String)p2.first()).equalsIgnoreCase(user)) {
							longLines = false;
							break;
						}
						if(((String)p.second()).length()>hFloodLength) {
							longLines = true;
						}
						else {
							longLines = false;
							break;
						}
					}

					if((nearFlood && stamp-last < floodTimeout) || (userCount>= floodLines && stamp-last < floodTimeout+1000)) {
						if(nearFloodUser1.equals(ircName) && nearFloodUser2.equals(ircName)) {
							nearFlood = true;
						}
						else if(nearFloodUser1.equals(ircName)) {
							nearFloodUser2 = ircName;
							nearFlood = false;
						}
						else {
							nearFloodUser1 = ircName;
							nearFloodUser2 = "";
							nearFlood = false;
						}
						System.out.println("nearFlood1="+nearFloodUser1+" nearFlood2="+nearFloodUser2+" stamp="+stamp+" last="+last+" floodTimeout="+floodTimeout);
					}
					else {
						nearFlood = false;
					}

					boolean shitUser = isJavaClient(ircName) || (nickTranslation.containsKey(ircName) && shitList.contains(((String)nickTranslation.get(ircName)).toLowerCase())) || shitList.contains(user.toLowerCase());
					if(longLines) {
						if(stamp-last < floodTimeout) {
							synchronized(out) {
								writeMsg(myChan,"Horizontal floods are floods too.");
								if(lastFloodUser.equalsIgnoreCase(user) || shitUser || (nickTranslation.containsKey(ircName) && lastFloodUser.equalsIgnoreCase(nickTranslation.get(ircName).toString()))) {
									writeMsg(myChan,"Welcome to bannage, "+user);
									write("KICK "+myChan+" "+user+" :Excess Flood, don't come back");
									if(shitUser) {
										write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
									}
									else {
										if(ircName.indexOf("@")>6) {
											write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")-6));
										}
										else {
											write("MODE "+myChan+" +b *!*"+ircName);
										}
									}
									if(nickTranslation.containsKey(ircName)) {
										if(!shitList.contains(((String)nickTranslation.get(ircName)).toLowerCase())) {
											shitList.addElement(((String)nickTranslation.get(ircName)).toLowerCase());
										}
									}								
								}
								else {
									writeMsg(myChan,"Do it again and you're banned");
									write("KICK "+myChan+" "+user+" :Excess Flood, use Fuyutsuki for pastes, retard");
								}
							}
							if(nickTranslation.containsKey(ircName)) {
								lastFloodUser = ((String)nickTranslation.get(ircName)).toLowerCase();
							}
							else {
								lastFloodUser = user;
							}
						
						}
					}
					else if(userCount >= floodLines || nearFlood) {
						if(stamp-last < floodTimeout || nearFlood) {
							synchronized(out) {
								writeMsg(myChan,"Stop flooding: "+userCount+" lines sent to channel in the last "+(stamp-last)+"ms");
								if(lastFloodUser.equalsIgnoreCase(user) || shitUser || (nickTranslation.containsKey(ircName) && lastFloodUser.equalsIgnoreCase(nickTranslation.get(ircName).toString()))) {
									writeMsg(myChan,"Welcome to bannage, "+user);
									write("KICK "+myChan+" "+user+" :Excess Flood, don't come back");
									if(shitUser) {
										write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
									}
									else {
										if(ircName.indexOf("@")>6) {
											write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")-6));
										}
										else {
											write("MODE "+myChan+" +b *!*"+ircName);
										}
									}
									if(nickTranslation.containsKey(ircName)) {
										if(!shitList.contains(((String)nickTranslation.get(ircName)).toLowerCase())) {
											shitList.addElement(((String)nickTranslation.get(ircName)).toLowerCase());
										}
									}								
								}
								else {
									writeMsg(myChan,"Do it again and you're banned");
									write("KICK "+myChan+" "+user+" :Excess Flood, use Fuyutsuki for pastes, retard");
								}
							}
							if(nickTranslation.containsKey(ircName)) {
								lastFloodUser = ((String)nickTranslation.get(ircName)).toLowerCase();
							}
							else {
								lastFloodUser = user;
							}
						}
						else if(shitUser && (stamp-last < floodTimeout * floodMultiplier)) {
							synchronized(out) {
								writeMsg(myChan,"Stop flooding asshole: "+userCount+" lines sent to channel in the last "+(stamp-last)+"ms");
								writeMsg(myChan,"Welcome to bannage, "+user);
								write("KICK "+myChan+" "+user+" :Excess Flood, don't come back");
								if(shitUser) {
									write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
								}
								else {
									if(ircName.indexOf("@")>6) {
										write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")-6));
									}
									else {
										write("MODE "+myChan+" +b *!*"+ircName);
									}
								}
								if(nickTranslation.containsKey(ircName)) {
									if(!shitList.contains(((String)nickTranslation.get(ircName)).toLowerCase())) {
										shitList.addElement(((String)nickTranslation.get(ircName)).toLowerCase());
									}
								}								
							}
						}
					}
					else if(shitUser) {
						if(line.length()>250) {
							synchronized(out) {
								writeMsg(myChan,"Stop flooding shithead");
								if(lastFloodUser.equalsIgnoreCase(user) || (nickTranslation.containsKey(ircName) && lastFloodUser.equalsIgnoreCase(nickTranslation.get(ircName).toString()))) {
									writeMsg(myChan,"Welcome to bannage, "+user);
									write("KICK "+myChan+" "+user+" :Bye, you can try to petition to Ornette, but his client ignores /msg floods");
									if(shitUser) {
										write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
									}
									else {
										if(ircName.indexOf("@")>6) {
											write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")-6));
										}
										else {
											write("MODE "+myChan+" +b *!*"+ircName);
										}
									}
									//								write("MODE "+myChan+" +b *!*Stupid@*");
								}
								else {
									writeMsg(myChan,"One more time and you're banned");
									write("KICK "+myChan+" "+user+" :Horizontal Floods are Floods too, dumbass.");
								}
							}
							if(nickTranslation.containsKey(ircName)) {
								lastFloodUser = ((String)nickTranslation.get(ircName)).toLowerCase();
							}
							else {
								lastFloodUser = user;
							}
						}
					}
				}
			}
		}
	}

	public void parseStats(String line,String user)
	{
		delay(300,200);
		writeMsg(myChan,"Channel statistics since "+channelStatsDate+", Number of messages sent to the channel by user:");
		Enumeration e = channelStats.keys();
		StringBuffer buffer = new StringBuffer();
		Vector v = new Vector();
		int count = 0;
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String statCount = (String)channelStats.get(key);
			v.addElement(new Pair(statCount,key));
		}
		boolean sorted = false;
		Pair[] list = new Pair[v.size()];
		for(int x=0;x<list.length;x++) {
			list[x] = (Pair)v.elementAt(x);
		}
		while(!sorted) {
			sorted = true;
			for(int x=0;x<list.length-1;x++) {
				if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
					Pair tmp = list[x];
					list[x] = list[x+1];
					list[x+1] = tmp;
					sorted = false;
				}
			}
		}

		for(int x=0;x<list.length;x++) {
			buffer.append(list[x].second()+"="+list[x].first()+", ");
			if(x%6==5) {
				delay(300,100);
				writeMsg(myChan,buffer.toString());
				buffer = new StringBuffer();
				count++;
				if(count>=3) {
					break;
				}
			}
		}
		if(buffer.length()>0) {
			delay(300,100);
			writeMsg(myChan,buffer.toString());
		}
		delay(300,100);
		writeMsg(myChan,"Number of times '...' has been used in this channel: "+dotStats);
		statsTime = System.currentTimeMillis();
	}

	public void parseMsg(String line,String user,String ircName)
	{
		try {
			String originalLine = line;
			line = line.substring(line.indexOf("PRIVMSG "+myNick+" :")+("PRIVMSG "+myNick+" :").length()).trim();
			if(user.equals(adminUser)) {
				admin(line,adminUser);
			}
			else if(nickTranslation.containsKey(ircName) && ((String)nickTranslation.get(ircName)).equals("Ornette")) {
				admin(line,"Ornette");
			}
			else {
				if(!line.startsWith("VERSION")) {
					updateStats(user,ircName,"msg");
					if(line.toLowerCase().startsWith("trivia")) {
						if(System.currentTimeMillis()-triviaTime > triviaTimeout2) {
							writeMsg(user,lastAnswer);
						}
						else {
							writeMsg(user,"I'm not ready to give out the answer yet");
						}
						updateStats(user,ircName,"answer");
					}
					else if(line.toLowerCase().startsWith("trigger")) {
						if(nickTranslation.containsKey(ircName) && !lastResponseUser.equals((String)nickTranslation.get(ircName))) {
							line = line.substring(7).trim();
							if(!filterVector.contains(line.trim()) && line.trim().length()<80 && line.trim().length()>1) {
								if(filterWordTimeouts.containsKey(user)) {
									long lastTime = Long.valueOf((String)filterWordTimeouts.get(user)).longValue();
									if(System.currentTimeMillis() - lastTime > 15000) {
										synchronized(this) {
											filterVector.addElement(line);
											filterVector.removeElementAt(0);
											filterWordTimeouts.put(user,""+System.currentTimeMillis());
										}
										writeMsg(user,"I've added '"+line+"' to the list of my trigger word responses.");
										updateStats(user,ircName,"responses");
										lastResponseUser = (String)nickTranslation.get(ircName);
									}
									else {
										writeMsg(user,"Sorry, wait a bit longer before inserting another message into the queue.");
									}
								}
								else {
									synchronized(this) {
										filterVector.addElement(line);
										filterVector.removeElementAt(0);
										filterWordTimeouts.put(user,""+System.currentTimeMillis());
									}
									writeMsg(user,"I've added '"+line+"' to the list of my trigger word responses.");
									updateStats(user,ircName,"responses");
									lastResponseUser = (String)nickTranslation.get(ircName);
								}
							}
							else {
								writeMsg(user,"I don't like your trigger response.");
							}
						}
						else {
							writeMsg(user,"Sorry, you need to wait for someone else to add a response before you can add another one.");
						}
					}
					else if(line.toLowerCase().startsWith("whois")) {
						line = line.substring(5).trim();
						if(userList.containsKey(line.toLowerCase())) {
							String ircName2 = (String)userList.get(line.toLowerCase());
							if(nickTranslation.containsKey(ircName2)) {
								String realName = (String)nickTranslation.get(ircName2);
								writeMsg(user,UNDERLINE+line+UNDERLINE+" is really "+BOLD+realName+BOLD);
							}
							else {
								writeMsg(user,"I don't seem to know who "+UNDERLINE+line+UNDERLINE+" is supported to be. I've been foiled.");
							}
						}
						else {
							writeMsg(user,"I don't seem to know who "+UNDERLINE+line+UNDERLINE+" is. Then again, I'm retarded.");
						}
					}
					else if(line.toLowerCase().startsWith("stats")) {
						printStats(user,null);
					}
					else if(line.startsWith("SCRIPT")) {
						parseScript(originalLine,user,ircName);
					}
					else if(line.startsWith("BUY")) {
						if(line.trim().equals("BUY")) {
							writeMsg(user,"You can /msg me with one of the following things you want to buy: 'trigger', 'voice', 'topic', 'kick', 'kickword', 'cannon', or 'halfops'");
							writeMsg(user,"Append any one of those after the '/msg "+myNick+" BUY' to buy that item using your chan dollars.");
							writeMsg(user,"For 'trigger', 'topic' and 'kickword', append your submission at the end. Example: /msg "+myNick+" BUY trigger snausages");
							writeMsg(user,buyTable.getPriceString());
						}
						else {
							String realUser = (String)nickTranslation.get(ircName).toLowerCase();
							String item = line.substring(3).trim();
							if(item.toLowerCase().startsWith("trigger")) {
								int index = item.indexOf(" ");
								if(index!=-1) {
									item = item.substring(index).trim();
									if(item.length()>5) {
										if(buyTable.buyTrigger(realUser)) {
											filterWord = item;
											writeMsg(user,"The trigger word is now "+filterWord);
											writeMsg(myChan,user+" has just paid to set the trigger word. I wonder what it could be...");
										}
										else {
											writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
										}
									}
									else {
										writeMsg(user,"I don't like that word, try again if you want");
									}
								}
							}
							else if(item.toLowerCase().startsWith("voice")) {
								if(buyTable.buyVoice(realUser)) {
									writeMsg(myChan,user+" has just paid to get voice in the channel, let's all congratulate the stupidity");
									write("MODE "+myChan+" +v "+user);
								}
								else {
									writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
								}
							}
							else if(item.toLowerCase().startsWith("cannon")) {
								if(buyTable.buyCannon(realUser)) {
									writeMsg(myChan,user+" has just paid to reset the kick cannon. Let the kicking begin!");
									kickAttempts = new Vector();
									writeMsg("Ornette",user+" reset the kick cannon");
								}
								else {
									writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
								}
							}
							else if(item.toLowerCase().startsWith("topic")) {
								int index = item.indexOf(" ");
								if(index!=-1) {
									item = item.substring(index).trim();
									if(buyTable.buyTopic(realUser)) {
										writeMsg(user,"The changing the topic to: "+item);
										writeMsg(myChan,user+" has just paid to change the topic, weeeeeeee");
										write("TOPIC "+myChan+" :"+item);
									}
									else {
										writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
									}
								}
							}
							else if(item.toLowerCase().startsWith("kickword")) {
								int index = item.indexOf(" ");
								if(index!=-1) {
									item = item.substring(index).trim();
									if(item.length()>5 && !userList.contains(item.toLowerCase())) {
										if(buyTable.buyKickword(realUser)) {
											writeMsg(user,"Added the kick word "+item);
											writeMsg(myChan,user+" has just paid for a kick word, can anyone guess what it is?");
											kickWords.addElement(item);
											addedKickWords.addElement(new Pair(item,System.currentTimeMillis()+""));
										}
										else {
											writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
										}
									}
									else {
										writeMsg(user,"I don't like that word, try again if you want");
									}
								}
							}
							else if(item.toLowerCase().startsWith("kick")) {
								int index = item.indexOf(" ");
								if(index!=-1) {
									item = item.substring(index).trim();
									if(item.equalsIgnoreCase(myNick)) {
										writeMsg(user,"Nice try, douche nozzle");
									}
									else {
										if(buyTable.buyKick(realUser)) {
											if(userList.containsKey(item.toLowerCase())) {
												writeMsg(myChan,user+" has just paid for a kick, fuck yeah!");
												delay(5000,1500);
												write("KICK "+myChan+" "+item+" :You were kicked because "+user+" paid for it in chan dollars");
											}
											else {
												writeMsg(myChan,user+" has just paid for a kick, but the dumbass tried to kick someone who isn't even in the channel. HA! Loser.");
											}
										}
										else {
											writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
										}
									}
								}
							}
							else if(item.toLowerCase().startsWith("halfops")) {
								if(buyTable.buyHalfops(realUser)) {
									writeMsg(myChan,"Holy FUCK, "+user+" just paid for halfops! EVERYONE! KICK THIS BASTARD FROM THE CHANNEL, HOLY SHIT");
									write("MODE "+myChan+" +h "+user);
								}
								else {
									writeMsg(user,"You either don't have enough chan dollars or you bought something recently");
								}
							}
							else {
								writeMsg(user,"WUT?");
							}
						}
					}
					else {
						if(triggerAward!=null && triggerAward.equalsIgnoreCase(user)) {
							triggerAward = null;
							if(line.length()>5) {
								filterWord = line.toLowerCase();
								writeMsg(user,"The trigger word is now '"+filterWord+"'");
							}
							else {
								writeMsg(user,"I don't like that word, you lose the award");
							}
						}
						else if(kickWordAward!=null && kickWordAward.equalsIgnoreCase(user)) {
							kickWordAward = null;
							if(line.length()>7 && !userList.containsKey(line.toLowerCase())) {
								writeMsg("ornette","adding temp kick word = "+line);
								kickWords.addElement(line);
								addedKickWords.addElement(new Pair(line,System.currentTimeMillis()+""));
								writeMsg(user,"Added '"+line+"' to the list of kick words");
							}
							else {
								writeMsg(user,"I don't like that word, you lose the award");
							}
						}
						else if(kickAward!=null && kickAward.equalsIgnoreCase(user)) {
							kickAward = null;
							if(line.equalsIgnoreCase(myNick)) {
								writeMsg(user,"You think I'm stupid enough to kick myself? You lose.");
							}
							else {
								writeMsg(myChan,user+" is awarded with a free kick, let's see who's the lucky victim...");
								delay(10000,2000);
								write("KICK "+myChan+" "+line+" :You were kicked by "+user);
							}
						}
						else if(topicAward!=null && topicAward.equalsIgnoreCase(user)) {
							topicAward = null;
							writeMsg(user,"Changing the topic to: "+line);
							writeMsg(myChan,user+" has been awarded with a topic change, let's see what it gets changed to...");
							delay(10000,2000);
							write("TOPIC "+myChan+" :"+line);
						}
						else if(loliMessageAward!=null && loliMessageAward.equalsIgnoreCase(user)) {
							loliMessageAward = null;
							loliMessage = "&lt;"+user+"&gt; "+encode(line);
							if(loliMessage.length()>100) {
								String tmpLong = "";
								int index = 0;
								int index2 = 100;
								String tmp = loliMessage.substring(index,index2);
								while(true) {
									if(tmp.lastIndexOf(" ")!=-1) {
										index2 = tmp.lastIndexOf(" ")+index;
									}
									tmpLong = tmpLong+loliMessage.substring(index,index2)+"<BR>";
									index = index2+1;
									index2 = index+100;
									if(index2>=loliMessage.length()) {
										tmpLong = tmpLong+loliMessage.substring(index);
										break;
									}
									tmp = loliMessage.substring(index,index2);
								}
								loliMessage = tmpLong;
							}
							writeMsg(user,"Your message has been added to the channel stats page"); 
						}
						else if(randomAward!=null && randomAward.equalsIgnoreCase(user)) {
							randomAward = null;
							randomMessage = line;
							writeMsg(user,"Changed the internal message to '"+line+"'");
							writeMsg(user,"Keep an eye out for it");
						}
						else {
							delay(300,100);
							checkFailedTrigger(user,ircName);
							synchronized(out) {
								writeMsg(user,"I don't understand.");
								delay(1200,200);
								writeMsg(user,"If you say '"+BOLD+"whois"+BOLD+"' followed by a nick of someone in the channel, I can try to tell you who it is.");
								delay(1220,200);
								writeMsg(user,"If you say '"+BOLD+"trivia"+BOLD+"', I'll give you the answer to the last trivia question.");
								delay(1220,200);
								writeMsg(user,"If you say '"+BOLD+"trigger"+BOLD+"' followed by a message, I'll add your message to my list of responses to the trigger word");
								delay(1220,200);
								writeMsg(user,"If you say '"+BOLD+"SCRIPT"+BOLD+"' followed by an episode (1-26) followed by a case-sensitive search term, I'll search my database of Eva episodes");
								delay(1220,200);
								writeMsg(user,"If you say '"+BOLD+"STATS"+BOLD+"', I'll give you my statistics");
								delay(1220,200);
								writeMsg(user,"I listen in the channel for '"+BOLD+"FORTUNE"+BOLD+"', '"+BOLD+"ROLL"+BOLD+"', '"+BOLD+"8BALL"+BOLD+"', '"+BOLD+"TRIVIA"+BOLD+"', '"+BOLD+"GAME"+BOLD+"', '"+BOLD+"PANTSU"+BOLD+"', '"+BOLD+"BOOBIES"+BOLD+"', '"+BOLD+"YOUTUBE"+BOLD+"', '"+BOLD+"MOAR+"+BOLD+"', '"+BOLD+"LESS"+BOLD+"', '"+BOLD+"LOLI"+BOLD+"', '"+BOLD+"WORTH"+BOLD+"', '"+BOLD+"NORIO"+BOLD+"', '"+BOLD+"KICK"+BOLD+" (only once)', '"+BOLD+"BUY"+BOLD+"', and '"+BOLD+"SCRIPT"+BOLD+"'. And, the triggered word, of course.");
							}
						}
					}
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void parseQuit(String line,String user,String ircName)
	{

		String trans = "Who the FUCK?";
		if(line.indexOf("*.net *.split")!=-1) {
			netsplit.addElement(user.toLowerCase());
		}
		else if(!isJavaClient(ircName)) {
			if(nickTranslation.containsKey(ircName)) {
				trans = (String)nickTranslation.get(ircName);
			}
			int random = Math.abs(r.nextInt(greetingProbability));
			if(random==0) {
				delay(300,100);
				char c = (char)1;
				writeMsg(myChan,c+"ACTION slams the door on "+user+c);
			}
			else if(random==1) {
				delay(300,100);
				writeMsg(myChan,"I understand, "+user+". Give my regards to Yui.");
			}
			else if(random==2) {
				delay(300,100);
				writeMsg(myChan,user+", did you finally meet Yui, too?");
			}
			else if(random==3) {
				delay(300,100);
				writeMsg(myChan,"And then, there were "+(userList.size()-1));
			}
			else if(random==4) {
				if(trans!=null && !trans.equalsIgnoreCase(user)) {
					writeMsg(myChan,"Later, "+trans);
				}
			}
			/*
			  synchronized(nickTranslation) {
			  Enumeration e = nickTranslation.keys();
			  while(e.hasMoreElements()) {
			  String key = (String)e.nextElement();
			  if(trans.equalsIgnoreCase((String)nickTranslation.get(key))) {
			  System.out.println("Removing from nickTranslation: "+key+"="+nickTranslation.remove(key));
			  }
			  }
			  }
			*/
		}
		recentVoice.remove(user.toLowerCase());
		userList.remove(user.toLowerCase());
		//		System.out.println("USERLIST="+userList);
	}


	public void parseJoin(String line,String user,String ircName)
	{
		if(isJavaClient(ircName)) {
			writeMsg(user,"Since you are using a client which randomly generates an identity ("+ircName+"), I will need to ignore you and not track any of your stats. Get a real IRC client or setup your identity correctly by filling out your info.");
		}
		else {
			//		System.out.println("****** "+nickChangeKick+" last="+lastNickChangeKick+" ircName="+ircName+" period="+(System.currentTimeMillis()-lastNickChangeKickTime));
			if(nickChangeKick && ircName.equals(lastNickChangeKick) && (System.currentTimeMillis()-lastNickChangeKickTime < autojoinTimeout) && !user.startsWith("Guest")) {
				writeMsg(myChan,"I'm currently in anal-nick-change mode, you just got kicked and seems you've got autojoin turned on, I've got no recourse except to ban you.");
				write("MODE "+myChan+" +b *!"+ircName);
				write("KICK "+myChan+" "+user+" :Sorry, you'd just keep coming back and getting kicked, /msg Ornette when you're done or turn off auto-join");
			}
			else {
				boolean kick = false;
				String origUser = user;
				userList.put(user.toLowerCase(),ircName);
				if(mostInChannel < userList.size()) {
					mostInChannel = userList.size();
					lastMostInChannel = System.currentTimeMillis();
				}
				//			System.out.println("USERLIST="+userList);

				if(netsplit.contains(user.toLowerCase())) {
					netsplit.remove(user.toLowerCase());
					if(ircName.trim().length()>0 && !nickTranslation.containsKey(ircName)) {
						System.out.println("Adding to nickTranslation: "+ircName+"="+user);
						nickTranslation.put(ircName,user);
					}
				}
				else {
					if(ircName.trim().length()>0 && !nickTranslation.containsKey(ircName)) {
						System.out.println("Adding to nickTranslation: "+ircName+"="+user);
						nickTranslation.put(ircName,user);
					}
					else {
						user = (String)nickTranslation.get(ircName);
					}

					Vector lastJoins = new Vector();
					if(joins.containsKey(user)) {
						lastJoins = (Vector)joins.get(user);
						lastJoins.addElement(System.currentTimeMillis()+"");
						if(lastJoins.size()>3) {
							lastJoins.removeElementAt(0);
						}
					}
					else {
						joins.put(user,lastJoins);
						lastJoins.addElement(System.currentTimeMillis()+"");
					}
					if(lastJoins.size()==3) {
						long firstJoin = Long.valueOf((String)lastJoins.elementAt(0)).longValue();
						long delay = System.currentTimeMillis()-firstJoin;
						System.out.println("delay="+delay);
						if(delay < joinSpamTimeout) {
							kick = true;						
							if(ircName.indexOf("@")>6) {
								write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")-6));
							}
							else {
								write("MODE "+myChan+" +b *!*"+ircName);
							}
							write("KICK "+myChan+" "+origUser+" :Too many joins, fix your client or your connection (3 joins in "+delay+")");

							userList.remove(origUser.toLowerCase());
						}
					}

					if(!kick) {
						if(user.equalsIgnoreCase("Ornette") || user.equalsIgnoreCase("jonlin")) {
							delay(300,100);
							writeMsg(myChan,"Master is in the house");
						}
						else if(user.equalsIgnoreCase("BrikHaus")) {
							delay(300,100);
							writeMsg(myChan,"Patients want medicine, but they all deserve a GIGA DRILL BREAK");
							//					writeMsg(myChan,"OH HE SO PRINGLES! WHERE YO CURLEH MUSTACHE AT?");
							delay(5000,2000);
							//							write("MODE "+myChan+" +h "+origUser);
						}
						else if(user.equalsIgnoreCase("chee_")) {
							delay(300,100);
							//				writeMsg(myChan,"Oshii "+RED+"sucks"+END+"...");
							writeMsg(myChan,"chee, you Foucault-loving fuck...");
						}
						else if(user.equalsIgnoreCase(myNick)) {
							delay(300,100);
							writeMsg(myChan,"Ok "+UNDERLINE+"fuckers"+UNDERLINE+", I'm back to make sure everyone has a miserable time");
						}
						else if(user.equalsIgnoreCase("Xeroko")) {
							delay(300,100);
							writeMsg(myChan,"Her boots were maid for walking~");
						}
						else if(user.equalsIgnoreCase("BobBQ")) {
							delay(300,100);
							//							writeMsg(myChan,"Would you put that in a memo, and entitle it "+BOLD+"\"Shit I Already Know?!\BOLD);
							//							writeMsg(myChan,""+BOLD+"\"ANO NAMAE WASURENAI!\""+BOLD);
							//							writeMsg(myChan,"Boring shounen! Bland seinen! Futanari and yaoi! None of them will stop yuri on its triumphant march towards saving the planet!");
							writeMsg(myChan,"One of those First Battalion fuckers took a dump in my foxhole!");
						}
						else if(user.equalsIgnoreCase("LoganPayne")) {
							delay(300,100);
							writeMsg(myChan,"'Cause every girl's crazy 'bout a sharp dressed man!");
						}
						else if(user.equalsIgnoreCase("Reichu")) {
							delay(300,100);
							//				writeMsg(myChan,"ALL HAIL "+BOLD+"REICHU"+BOLD+"!");
							char c = (char)1;
							//							writeMsg(myChan,"Reichu, you phailpus loving fuck...");
							//							writeMsg(myChan,"I always knew there was something wrong with me that I couldn't put into words.");
							writeMsg(myChan,"Kellogg's Corn Flakes: for a hedonistically barren and morally righteous start to each day!");
							delay(5000,2000);
							//							write("MODE "+myChan+" +h "+origUser);
						}
						else if(user.equalsIgnoreCase("themaninblack")) {
							delay(300,100);
							writeMsg(myChan,"Socialism makes me wet");
						}
						/*
						  else if(user.equalsIgnoreCase("Kaminoyouni")) {
						  delay(300,100);
						  //							writeMsg(myChan,"Has any seen Ritsuko lately?");
						  writeMsg(myChan,"snausages");
						  delay(5000,2000);
						  //							write("MODE "+myChan+" +h "+origUser);
						  }
						*/
						else if(user.equalsIgnoreCase("ActionBastard")) {
							//							delay(300,100);
							//							writeMsg(myChan,"Fuck, here's that shithead assfuck again...");
						}
						else if(user.equalsIgnoreCase("SSD")) {
							delay(300,100);
							writeMsg(myChan,"The "+UNDERLINE+"Forum Mascot/Goddess"+UNDERLINE+" is making' Gluten-free "+BOLD+"WAFF"+BOLD+"les!");
						}
						else if(user.equalsIgnoreCase("honsou")) {
							delay(300,100);
							writeMsg(myChan,"Hey honsou, everyone's doing anal. It's the new black.");
						}
						else if(user.equalsIgnoreCase("Omegagouki")) {
							delay(300,100);
							writeMsg(myChan,"Here's the guy who thinks thigh-highs with a skirt are hot. What a loser!");
						}
						else if(user.equalsIgnoreCase("flatulant")) {
							delay(300,100);
							writeMsg(myChan,"Who has the worst connection in the world? Oh wait! There he is!");
						}
						else if(user.equalsIgnoreCase("Tokster")) {
							delay(300,100);
							writeMsg(myChan,"Nandeyanen! ( http://www.tesuji.org/sounds/nandeyanen.wav )");
						}
						else if(user.equalsIgnoreCase("TerraChronicle")) {
							delay(300,100);
							writeMsg(myChan,"Now you must acquire a taste for: Freeform Jazz!");
						}
						else if(user.equalsIgnoreCase("Defectron")) {
							delay(300,100);
							writeMsg(myChan,"RAZENGAN OVERLOAD!!!");
						}
						else if(user.equalsIgnoreCase("CorporalChaos")) {
							delay(300,100);
							writeMsg(myChan,"Make way for the real life epic fail guy.");
						}
						else if(user.equalsIgnoreCase("QVMS")) {
							delay(300,100);
							char c = (char)1;
							writeMsg(myChan,c+"ACTION hands Ironfoot a brand new Rei and Asuka for his enjoyment"+c);
						}
						else if(user.equalsIgnoreCase("Anti_Goth")) {
							delay(300,100);
							writeMsg(myChan,"Curly hair beats esotericism");
						}
						else if(user.equalsIgnoreCase("ZapalacX")) {
							delay(300,100);
							//					writeMsg(myChan,"Wasuren na. Omae wo shinjiro. Ore ga shinjiru omae demo nai. Omae ga shinjiru ore demo nai. Omae ga shinjiru... OMAE WO SHINJIRO!");
							//							writeMsg(myChan,"ORE WO DARE DA TO OMOTTE-YAGARU!!!");
							//							writeMsg(myChan,"GABORAAAAAAAAAAAAAA!");
							writeMsg(myChan,"I belong to the organization known as \"Warashibe.\" I have come to make an exchange for this bag of farts.");
							delay(5000,2000);
							//							write("MODE "+myChan+" +h "+origUser);
						}
						else if(user.equalsIgnoreCase("Hunter21")) {
							delay(300,100);
							writeMsg(myChan,"Just because you are tired and stressed doesn't give you the right to be a grumpy and cuss out the weeaboos and b-tards on this channel.");
						}
						else if(user.equalsIgnoreCase("AchtungAffen")) {
							delay(300,100);
							//				writeMsg(myChan,"AchtungAffen: I would prefer to jackoff a bull or a horse rather than a pig");
							writeMsg(myChan,"ZETSUBOUSHITA!!! THIS WORLD WHERE NO ONLINE STORE WILL SHIP MY REVOLTECH FRAULEIN REI TO ARGENTINA HAS LEFT ME IN DESPAIR!!!");
						}
						else if(user.equalsIgnoreCase("UrsusArctos")) {
							char c = (char)1;
							delay(300,100);
							writeMsg(myChan,c+"ACTION torpedoes himself while singing the Ode to Joy."+c);
						}
						else if(user.equalsIgnoreCase("zuggy")) {
							delay(300,100);
							writeMsg(myChan,"This is a "+BOLD+"case"+BOLD+" for Mulder and Scully.");
						}
						else if(user.equalsIgnoreCase("eric_blair")) {
							delay(300,100);
							writeMsg(myChan,"\"He doesn't always drink beer, but when he does, he prefers whiskey\"");
						}
						else {
							delay(300,100);
							int ran = Math.abs(r.nextInt(greetingProbability));
							if(ran==0) {
								writeMsg(myChan,BOLD+user+BOLD+": did you bring bacon?");
							}
							else if(ran==1) {
								writeMsg(myChan,"Who's ready to smoke a joint?");
							}
							else if(ran==2) {
								char c = (char)1;
								writeMsg(myChan,c+"ACTION sniffs "+user+"'s butt"+c);
							}
							else if(ran==3) {
								char c = (char)1;
								writeMsg(myChan,c+"ACTION slaps "+user+" in the back of the head."+c);
								writeMsg(myChan,"lolpwned");
							}
							else if(ran==4 || ran==5 || ran==6) {
								writeMsg(myChan,"Welcome "+BOLD+user);
							}
						}
					}
				}
			}
		}
	}

	public void parseNick(String line,String user,String ircName)
	{
		String newuser = line.substring(line.lastIndexOf(":")+1);
		
		if(nickChangeKick) {
			lastNickChangeKick = ircName;
			writeMsg(myChan,"I'm currently in anal-nick-change mode. See (under Nick Changes): http://help.dal.net/irchelp/annoyance.php");
			write("KICK "+myChan+" "+newuser+" :If you are away, set your away flag, if you are RPing, go RP then come back when you're done instead of flooding this channel");
			lastNickChange = System.currentTimeMillis();
		}
		else {
			if((System.currentTimeMillis() - lastNickChange) < nickTimeout) {
				nickChangeKick = true;
				System.out.println("----ANAL NICK CHANGE: ON");
			}
			lastNickChange = System.currentTimeMillis();
				
			if(!isJavaClient(ircName)) {
				userList.remove(user.toLowerCase());
				userList.put(newuser.toLowerCase(),ircName);
				//			System.out.println("USERLIST="+userList);
					
				if(newuser.equalsIgnoreCase("Ornette")) {
					delay(300,100);
					writeMsg(myChan,"I'm "+UNDERLINE+"Ornette"+UNDERLINE+"'s bitch, that is all");
				}
				else if(newuser.equalsIgnoreCase("chee_")) {
					delay(300,100);
					writeMsg(myChan,"and also: "+UNDERLINE+"Oshii sucks"+UNDERLINE);
				}
				else if(newuser.equalsIgnoreCase("BrikHaus")) {
					delay(300,100);
					writeMsg(myChan,BOLD+"CURLEH MUSTACHE"+BOLD);
				}
				else if(newuser.equalsIgnoreCase("Kaminoyouni") || newuser.equalsIgnoreCase("LoganPayne")) {
					delay(300,100);
					writeMsg(myChan,"Welcome back "+newuser);
				}
				else if(newuser.equalsIgnoreCase("ssd")) {
					delay(300,100);
					writeMsg(myChan,"SSD"+BOLD+"SSD"+BOLD+""+UNDERLINE+"SSD"+UNDERLINE+PURPLE+"SSD"+END+CYAN+"SSD"+END+RED+"SSD"+END+PURPLE+"SSD"+END+BLUE+"SSD"+END+GREEN+"SSD"+END+BOLD+"SSD"+BOLD+RED+"SSD"+END+PURPLE+"SSD"+END+"SSD?");
				}
				else {
					int ran = Math.abs(r.nextInt(greetingProbability));
					if(ran==0) {
						delay(300,100);
						writeMsg(myChan,"Why would "+user+" want to be "+BOLD+newuser+BOLD+"?");
					}
					else if(ran==1) {
						delay(300,100);
						writeMsg(myChan,"My anti-faggot senses are tingling...");
					}
					else if(ran==2 || ran==3) {
						if(nickTranslation.containsKey(ircName)) {
							String realUser = (String)nickTranslation.get(ircName);
							if(!realUser.equals(newuser) && !realUser.equals(user)) {
								delay(300,100);
								writeMsg(myChan,"(Psst, "+newuser+" is REALLY "+realUser+")");
							}
						}
					}
				}
			}
		}

		lastNickChange = System.currentTimeMillis();
	}

	public void parseScript(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			int index = line.indexOf("SCRIPT");
			line = line.substring(index);
			index = line.indexOf(" ");
			updateStats(user,ircName,"script");
			if(index!=-1) {
				line = line.substring(index+1).trim();
				index = line.indexOf(" ");
				if(index!=-1) {
					String ep = line.substring(0,index).trim();
					String words = line.substring(index).trim();
			
					System.out.println("*************SCRIPT: episode="+ep+" search terms="+words);
				
					if(ep.equals("21") || ep.equals("22") || ep.equals("23") || ep.equals("24")) {
						ep = ep+"_npc_adv_platinum.txt";
					}
					else if(ep.equals("1") || ep.equals("2") || ep.equals("3") || ep.equals("4") || ep.equals("5") || ep.equals("6") || ep.equals("7") || ep.equals("8") || ep.equals("9")) {
						ep = "0"+ep+"_adv_platinum.txt";
					}
					else {
						ep = ep+"_adv_platinum.txt";
					}
				
					File parent = new File(scriptsDir);
					File episode = new File(scriptsDir,ep);
					if(episode.exists()) {
						StringBuffer contents = new StringBuffer();
						BufferedReader fileIn = null;
						try {
							fileIn = new BufferedReader(new FileReader(episode));
							String fileLine = fileIn.readLine();
							while(fileLine!=null) {
								if(fileLine.trim().equals("")) {
									if(contents.toString().indexOf(words)!=-1) {
										break;
									}
									contents = new StringBuffer();
								}
								else {
									contents.append(fileLine);
									contents.append("\n");
								}
								fileLine = fileIn.readLine();
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						try {
							fileIn.close();
						}
						catch(Exception e) {;}
						if(contents.toString().indexOf(words)==-1) {
							contents = new StringBuffer();
						}
						if(contents.length()>0) {
							if(contents.length()>1000) {
								writeMsg(user,"Sorry, search results are too long for IRC");
							}
							else {
								String fortune = contents.toString();
								index = fortune.indexOf("\n");
								synchronized(out) {
									while(index!=-1) {
										delay(1200,100);
										writeMsg(user,fortune.substring(0,index));
										fortune = fortune.substring(index+1);
										index = fortune.indexOf("\n");
									}
									if(fortune.trim().length()>0) {
										delay(1200,100);
										writeMsg(user,fortune);
									}
								}
							}
						}
						else {
							writeMsg(user,"Can't find what you're looking for");
						}
					}
					else {
						writeMsg(user,"Can't find that episode");
					}
				}
				else {
					writeMsg(user,"SCRIPT <ep#> <exact search pattern>");
				}
			}
			else {
				writeMsg(user,"SCRIPT <ep#> <exact search pattern>");
			}
		}
	}

	public void parseFortune(String line,String user,String ircName,String random)
	{
		if(!isJavaClient(ircName)) {
			boolean isRandom = random!=null;
			fortuneTime = System.currentTimeMillis();
			String fortuneCommand = fortuneProgram;
			if(line.length()>10) {
				fortuneCommand = fortuneCommand + " -o food children-of-dune startrek goedel knghtbrd news slack-fortunes-vol-4 ascii-art firefly law slack-fortunes-vol-13 off/astrology off/religion off/privates off/hphobia off/vulgarity off/racism off/zippy off/songs-poems off/definitions off/fortunes off/politics off/art off/miscellaneous off/debian off/black-humor off/drugs off/atheism off/riddles off/misandry off/linux off/misogyny off/sex off/cookie off/ethnic gentoo-dev perl starwars SP osfortune calvin science dubya slack-fortunes-vol-8 house-harkonnen slack-fortunes-vol-12 wisdom heretics-of-dune computers homer slack-fortunes-vol-2 zippy songs-poems house-atreides definitions fortunes slack-fortunes-vol-9 dune-messiah politics humorists slack-fortunes-vol-1 art pets slack-fortunes-vol-11 miscellaneous god-emperor translate-me chalkboard love paradoxum dune kernelcookies futurama chucknorris debian strangelove literature slack-fortunes-vol-7 slack-fortunes-vol-10 magic humorix-misc hitchhiker tao humorix-stories slack-fortunes-vol-6 people drugs education linuxcookie familyguy work kids slack-fortunes-vol-5 zx-error powerpuff riddles pqf medicine linux men-women chapterhouse-dune gentoo-forums bofh-excuses smac slack-fortunes-vol-3 sports platitudes cookie ethnic taow thomas.ogrisegg fortune-mod-woody-allen-it rss norbert.tretkowski fvl debilneho tarantino tron touhou";
			}
			int index = line.indexOf("FORTUNE");
			line = line.substring(index+7).trim();
			if(line.equals("LIST")) {
				synchronized(out) {
					writeMsg(myChan,"You can choose from these types of fortunes:");
					writeMsg(myChan,"food, news, science, politics, dubya, people, kids, law, love, wisdom, linux, computers, starwars, startrek, calvin, futurama, familyguy, homer, chucknorris, tron, starcraft, touhou");
					writeMsg(myChan,"offensive ones: astrology, religion, vulgarity, racism, black-humor, limerick, drugs, sex, ethnic, misogyny, misandry, privates, riddles, misc, tarantino");
				}
			}
			else {
				if(isRandom) {
					fortuneCommand = fortuneProgram + " children-of-dune startrek goedel firefly off/astrology off/religion off/privates off/vulgarity off/racism off/zippy off/fortunes off/politics off/black-humor off/drugs off/misandry off/misogyny off/sex off/ethnic starwars calvin house-harkonnen heretics-of-dune homer house-atreides dune-messiah politics chalkboard dune futurama chucknorris strangelove hitchhiker tao familyguy taow fortune-mod-woody-allen-it norbert.tretkowski tarantino tron -a -n 65 -s";
				}
				else if(line.equalsIgnoreCase("food")) {
					fortuneCommand = fortuneProgram + " food";
					if(!fortuneDatabaseStats.containsKey("food")) {
						fortuneDatabaseStats.put("food","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("food"));
						count++;
						fortuneDatabaseStats.put("food",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("news")) {
					fortuneCommand = fortuneProgram + " news";
					if(!fortuneDatabaseStats.containsKey("news")) {
						fortuneDatabaseStats.put("news","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("news"));
						count++;
						fortuneDatabaseStats.put("news",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("science")) {
					fortuneCommand = fortuneProgram + " science";
					if(!fortuneDatabaseStats.containsKey("science")) {
						fortuneDatabaseStats.put("science","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("science"));
						count++;
						fortuneDatabaseStats.put("science",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("politics")) {
					fortuneCommand = fortuneProgram + " politics";
					if(!fortuneDatabaseStats.containsKey("politics")) {
						fortuneDatabaseStats.put("politics","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("politics"));
						count++;
						fortuneDatabaseStats.put("politics",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("dubya")) {
					fortuneCommand = fortuneProgram + " dubya";
					if(!fortuneDatabaseStats.containsKey("dubya")) {
						fortuneDatabaseStats.put("dubya","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("dubya"));
						count++;
						fortuneDatabaseStats.put("dubya",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("tron")) {
					fortuneCommand = fortuneProgram + " tron";
					if(!fortuneDatabaseStats.containsKey("tron")) {
						fortuneDatabaseStats.put("tron","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("tron"));
						count++;
						fortuneDatabaseStats.put("tron",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("starcraft")) {
					fortuneCommand = fortuneProgram + " starcraft";
					if(!fortuneDatabaseStats.containsKey("starcraft")) {
						fortuneDatabaseStats.put("starcraft","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("starcraft"));
						count++;
						fortuneDatabaseStats.put("starcraft",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("people")) {
					fortuneCommand = fortuneProgram + " people";
					if(!fortuneDatabaseStats.containsKey("people")) {
						fortuneDatabaseStats.put("people","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("people"));
						count++;
						fortuneDatabaseStats.put("people",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("kids")) {
					fortuneCommand = fortuneProgram + " kids";
					if(!fortuneDatabaseStats.containsKey("kids")) {
						fortuneDatabaseStats.put("kids","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("kids"));
						count++;
						fortuneDatabaseStats.put("kids",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("law")) {
					fortuneCommand = fortuneProgram + " law";
					if(!fortuneDatabaseStats.containsKey("law")) {
						fortuneDatabaseStats.put("law","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("law"));
						count++;
						fortuneDatabaseStats.put("law",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("love")) {
					fortuneCommand = fortuneProgram + " love";
					if(!fortuneDatabaseStats.containsKey("love")) {
						fortuneDatabaseStats.put("love","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("love"));
						count++;
						fortuneDatabaseStats.put("love",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("wisdom")) {
					fortuneCommand = fortuneProgram + " wisdom";
					if(!fortuneDatabaseStats.containsKey("wisdom")) {
						fortuneDatabaseStats.put("wisdom","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("wisdom"));
						count++;
						fortuneDatabaseStats.put("wisdom",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("linux")) {
					fortuneCommand = fortuneProgram + " linux";
					if(!fortuneDatabaseStats.containsKey("linux")) {
						fortuneDatabaseStats.put("linux","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("linux"));
						count++;
						fortuneDatabaseStats.put("linux",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("computers")) {
					fortuneCommand = fortuneProgram + " computers";
					if(!fortuneDatabaseStats.containsKey("computers")) {
						fortuneDatabaseStats.put("computers","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("computers"));
						count++;
						fortuneDatabaseStats.put("computers",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("starwars")) {
					fortuneCommand = fortuneProgram + " starwars";
					if(!fortuneDatabaseStats.containsKey("starwars")) {
						fortuneDatabaseStats.put("starwars","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("starwars"));
						count++;
						fortuneDatabaseStats.put("starwars",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("startrek")) {
					fortuneCommand = fortuneProgram + " startrek";
					if(!fortuneDatabaseStats.containsKey("startrek")) {
						fortuneDatabaseStats.put("startrek","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("startrek"));
						count++;
						fortuneDatabaseStats.put("startrek",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("touhou")) {
					fortuneCommand = fortuneProgram + " touhou";
					if(!fortuneDatabaseStats.containsKey("touhou")) {
						fortuneDatabaseStats.put("touhou","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("touhou"));
						count++;
						fortuneDatabaseStats.put("touhou",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("calvin")) {
					fortuneCommand = fortuneProgram + " calvin";
					if(!fortuneDatabaseStats.containsKey("calvin")) {
						fortuneDatabaseStats.put("calvin","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("calvin"));
						count++;
						fortuneDatabaseStats.put("calvin",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("futurama")) {
					fortuneCommand = fortuneProgram + " futurama";
					if(!fortuneDatabaseStats.containsKey("futurama")) {
						fortuneDatabaseStats.put("futurama","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("futurama"));
						count++;
						fortuneDatabaseStats.put("futurama",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("familyguy")) {
					fortuneCommand = fortuneProgram + " familyguy";
					if(!fortuneDatabaseStats.containsKey("familyguy")) {
						fortuneDatabaseStats.put("familyguy","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("familyguy"));
						count++;
						fortuneDatabaseStats.put("familyguy",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("homer")) {
					fortuneCommand = fortuneProgram + " homer";
					if(!fortuneDatabaseStats.containsKey("homer")) {
						fortuneDatabaseStats.put("homer","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("homer"));
						count++;
						fortuneDatabaseStats.put("homer",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("chucknorris")) {
					fortuneCommand = fortuneProgram + " chucknorris";
					if(!fortuneDatabaseStats.containsKey("chucknorris")) {
						fortuneDatabaseStats.put("chucknorris","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("chucknorris"));
						count++;
						fortuneDatabaseStats.put("chucknorris",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("astrology")) {
					fortuneCommand = fortuneProgram + " off/astrology";
					if(!fortuneDatabaseStats.containsKey("astrology")) {
						fortuneDatabaseStats.put("astrology","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("astrology"));
						count++;
						fortuneDatabaseStats.put("astrology",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("religion")) {
					fortuneCommand = fortuneProgram + " off/religion";
					if(!fortuneDatabaseStats.containsKey("religion")) {
						fortuneDatabaseStats.put("religion","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("religion"));
						count++;
						fortuneDatabaseStats.put("religion",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("vulgarity")) {
					fortuneCommand = fortuneProgram + " off/vulgarity";
					if(!fortuneDatabaseStats.containsKey("vulgarity")) {
						fortuneDatabaseStats.put("vulgarity","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("vulgarity"));
						count++;
						fortuneDatabaseStats.put("vulgarity",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("racism")) {
					fortuneCommand = fortuneProgram + " off/racism";
					if(!fortuneDatabaseStats.containsKey("racism")) {
						fortuneDatabaseStats.put("racism","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("racism"));
						count++;
						fortuneDatabaseStats.put("racism",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("black-humor")) {
					fortuneCommand = fortuneProgram + " off/black-humor";
					if(!fortuneDatabaseStats.containsKey("black-humor")) {
						fortuneDatabaseStats.put("black-humor","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("black-humor"));
						count++;
						fortuneDatabaseStats.put("black-humor",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("limerick")) {
					fortuneCommand = fortuneProgram + " off/limerick";
					if(!fortuneDatabaseStats.containsKey("limerick")) {
						fortuneDatabaseStats.put("limerick","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("limerick"));
						count++;
						fortuneDatabaseStats.put("limerick",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("drugs")) {
					fortuneCommand = fortuneProgram + " off/drugs";
					if(!fortuneDatabaseStats.containsKey("drugs")) {
						fortuneDatabaseStats.put("drugs","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("drugs"));
						count++;
						fortuneDatabaseStats.put("drugs",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("sex")) {
					fortuneCommand = fortuneProgram + " off/sex";
					if(!fortuneDatabaseStats.containsKey("sex")) {
						fortuneDatabaseStats.put("sex","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("sex"));
						count++;
						fortuneDatabaseStats.put("sex",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("ethnic")) {
					fortuneCommand = fortuneProgram + " off/ethnic";
					if(!fortuneDatabaseStats.containsKey("ethnic")) {
						fortuneDatabaseStats.put("ethnic","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("ethnic"));
						count++;
						fortuneDatabaseStats.put("ethnic",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("misogyny")) {
					fortuneCommand = fortuneProgram + " off/misogyny";
					if(!fortuneDatabaseStats.containsKey("misogyny")) {
						fortuneDatabaseStats.put("misogyny","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("misogyny"));
						count++;
						fortuneDatabaseStats.put("misogyny",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("misandry")) {
					fortuneCommand = fortuneProgram + " off/misandry";
					if(!fortuneDatabaseStats.containsKey("misandry")) {
						fortuneDatabaseStats.put("misandry","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("misandry"));
						count++;
						fortuneDatabaseStats.put("misandry",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("privates")) {
					fortuneCommand = fortuneProgram + " off/privates";
					if(!fortuneDatabaseStats.containsKey("privates")) {
						fortuneDatabaseStats.put("privates","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("privates"));
						count++;
						fortuneDatabaseStats.put("privates",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("riddles")) {
					fortuneCommand = fortuneProgram + " off/riddles";
					if(!fortuneDatabaseStats.containsKey("riddles")) {
						fortuneDatabaseStats.put("riddles","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("riddles"));
						count++;
						fortuneDatabaseStats.put("riddles",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("tarantino")) {
					fortuneCommand = fortuneProgram + " tarantino";
					if(!fortuneDatabaseStats.containsKey("tarantino")) {
						fortuneDatabaseStats.put("tarantino","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("tarantino"));
						count++;
						fortuneDatabaseStats.put("tarantino",""+count);
					}
					catch(Exception e) {;}
				}
				else if(line.equalsIgnoreCase("misc")) {
					fortuneCommand = fortuneProgram + " off/miscellaneous";
					if(!fortuneDatabaseStats.containsKey("misc")) {
						fortuneDatabaseStats.put("misc","0");
					}
					try {
						int count = Integer.parseInt((String)fortuneDatabaseStats.get("misc"));
						count++;
						fortuneDatabaseStats.put("misc",""+count);
					}
					catch(Exception e) {;}
				}

				ExecuteCommand command = new ExecuteCommand(fortuneCommand);
				StringBuffer output = new StringBuffer();
				command.processOut = output;
				command.finishedWaitTime = (200 + r.nextInt(100));
				command.execute();
				String fortune = output.toString();
				index = fortune.indexOf("\n");
				Vector lines = new Vector();
				int charCount = 0;
				if(isRandom) {
					if(index!=-1) {
						lines.addElement(fortune.substring(0,index).trim());
					}
					else {
						lines.addElement(fortune.trim());
					}
				}
				else {
					while(index!=-1) {
						charCount = charCount+index;
						lines.addElement(fortune.substring(0,index).trim());
						fortune = fortune.substring(index+1);
						index = fortune.indexOf("\n");
					}
					if(fortune.trim().length()>0) {
						charCount = charCount+fortune.length();
						lines.addElement(fortune.trim());
					}
				}
				if(charCount>500) {
					writeMsg(myChan,"I don't feel like giving you a fortune right now, suck it.");
				}
				else if(lines.size()>8) {
					writeMsg(myChan,"I could give you a fortune, but you weren't going to like it.");
				}
				else {
					synchronized(out) {
						for(int x=0;x<lines.size();x++) {
							delay(100,50);
							writeMsg(myChan,""+lines.elementAt(x));
						}
					}
				}
				updateStats(user,ircName,"fortune");
			}
		}
	}

	public void parseMOAR(String line, String user)
	{
		pornTime = System.currentTimeMillis();
		int index = Math.abs(r.nextInt(alsImages.size()));
		System.out.println(line);
		try {
			String url = (String)alsImages.elementAt(index);
			delay(500,300);
			if(line.indexOf("MOAR ")!=-1) {
				String tag = line.substring(line.indexOf("MOAR ")+5);
				if(tag.trim().length()>0 && tag.trim().length()<=20) {
					String tmpurl = "http://sente.tesuji.org/als_archive/?random="+HTMLStringTools.encode(tag.trim().toLowerCase());
					String page = loader.getURL(tmpurl);
					index = page.indexOf("<!--INTERNAL:tag=");
					if(index!=-1) {	
						int index2 = page.indexOf("-->",index);
						if(index2!=-1) {
							tag = page.substring(index+17,index2).trim();
							index = page.indexOf("<!--INTERNAL:image=");
							if(index!=-1) {
								index2 = page.indexOf("-->",index);
								if(index2!=-1) {
									String image = page.substring(index+19,index2);
									url = "http://sente.tesuji.org/als_archive/"+HTMLStringTools.encode(tag.trim().toLowerCase())+"/"+image;
								}
							}
						}
					}
				}
			}
			writeMsg(myChan,BOLD+user+BOLD+" has requested some MOAR images: "+url);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void parseLESS(String line, String user)
	{
		pornTime = System.currentTimeMillis();
		String random = "bacon";
		try {
			ExecuteCommand command = new ExecuteCommand("./randomWord.sh");
			StringBuffer output = new StringBuffer();
			command.processOut = output;
			command.finishedWaitTime = (500 + r.nextInt(100));
			command.execute();
			line = output.toString().trim();
			random = line;
		}
		catch(Exception e) {e.printStackTrace();}
		
		try {
			String url= "http://images.google.com/images?svnum=10&um=1&hl=en&q="+random;
			String page = loader.getURL(url);
			Vector images = new Vector();
			int index = page.indexOf("/imgres?imgurl=http");
			while(index!=-1) {
				page = page.substring(index+3);
				index = page.indexOf("imgurl=http");
				if(index!=-1) {
					page = page.substring(index);
					index = page.indexOf("&");
					String imageURL = page.substring(7,index);
					index = imageURL.indexOf("%25");
					while(index!=-1) {
						imageURL = imageURL.substring(0,index) + "%" + imageURL.substring(index+3);
						index = imageURL.indexOf("%25");
					}
					images.addElement(imageURL);
				}
				else {
					break;
				}
				index = page.indexOf("/imgres?imgurl=http");
			}
			
			String image = (String)images.elementAt(Math.abs(r.nextInt(images.size())));
			if(image!=null) {
				delay(500,300);
				writeMsg(myChan,BOLD+user+BOLD+" has LESS "+BOLD+random+BOLD+": "+image);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void parseWakamoto(String line, String user, String ircName)
	{
		int i = Math.abs(r.nextInt(56));
		String sound = "http://www.tesuji.org/sounds/wakamoto/";
		if(i==0) {
			sound = sound+"abura.wav";
		}  
		else if(i==0) {
			sound = sound+"going_to_party_party_party.wav";
		}  
		else if(i==1) {
			sound = sound+"its_raining.wav";
		}  
		else if(i==2) {
			sound = sound+"no_way.wav";
		}
		else if(i==3) {
			sound = sound+"braaabraaa.wav";
		}  
		else if(i==4) {
			sound = sound+"hahaha.wav";
		}  
		else if(i==5) {
			sound = sound+"katabura.wav";
		}  
		else if(i==6) {
			sound = sound+"nomnomnomnom.wav";
		}
		else if(i==7) {
			sound = sound+"brrrrraaaa.wav";
		}  
		else if(i==8) {
			sound = sound+"hahahaha.wav";
		}  
		else if(i==9) {
			sound = sound+"limitations_reached.wav";
		}  
		else if(i==10) {
			sound = sound+"nwooooo.wav";
		}
		else if(i==11) {
			sound = sound+"carapacho.wav";
		}  
		else if(i==12) {
			sound = sound+"haigrrrraaa.wav";
		}  
		else if(i==13) {
			sound = sound+"listen_to_me.wav";
		}  
		else if(i==14) {
			sound = sound+"nwuuee.wav";
		}
		else if(i==15) {
			sound = sound+"chiiiiiiyooo.wav";
		}  
		else if(i==16) {
			sound = sound+"heheh.wav";
		}  
		else if(i==17) {
			sound = sound+"maximum_armor.wav";
		}  
		else if(i==18) {
			sound = sound+"receiving_female_radio_waves.wav";
		}
		else if(i==19) {
			sound = sound+"cloak.wav";
		}  
		else if(i==20) {
			sound = sound+"hello_everynyan.wav";
		}  
		else if(i==21) {
			sound = sound+"maximum_speed.wav";
		}  
		else if(i==22) {
			sound = sound+"reform.wav";
		}
		else if(i==23) {
			sound = sound+"donald_magic.wav";
		}  
		else if(i==24) {
			sound = sound+"how_are_you.wav";
		}  
		else if(i==25) {
			sound = sound+"maximum_strength.wav";
		}  
		else if(i==26) {
			sound = sound+"repent.wav";
		}
		else if(i==27) {
			sound = sound+"eehhhhh.wav";
		}  
		else if(i==28) {
			sound = sound+"i_am_the_headmaster.wav";
		}  
		else if(i==29) {
			sound = sound+"merry_christmas.wav";
		}  
		else if(i==30) {
			sound = sound+"saa.wav";
		}
		else if(i==31) {
			sound = sound+"energy_critical.wav";
		}  
		else if(i==32) {
			sound = sound+"i_have_some_more.wav";
		}  
		else if(i==33) {
			sound = sound+"nani.wav";
		}  
		else if(i==34) {
			sound = sound+"switch_on.wav";
		}
		else if(i==35) {
			sound = sound+"ero-ping.wav";
		}  
		else if(i==36) {
			sound = sound+"i_will_eat_it.wav";
		}  
		else if(i==37) {
			sound = sound+"nani_clip.wav";
		}  
		else if(i==38) {
			sound = sound+"tears.wav";
		}
		else if(i==39) {
			sound = sound+"gaboraaa.wav";
		}  
		else if(i==40) {
			sound = sound+"i_wish_i_were_a_bird.wav";
		}  
		else if(i==41) {
			sound = sound+"no_objections.wav";
		}  
		else if(i==42) {
			sound = sound+"unknown_clip.wav";
		}
		else if(i==43) {
			sound = sound+"very_shit.wav";
		}
		else if(i==44) {
			sound = sound+"unknown_clip2.wav";
		}
		else if(i==45) {
			sound = sound+"unknown_clip3.wav";
		}
		else if(i==46) {
			sound = sound+"very_melon.wav";
		}
		else if(i==47) {
			sound = sound+"yahoo.wav";
		}
		else if(i==48) {
			sound = sound+"what_am_i.wav";
		}
		else if(i==49) {
			sound = sound+"unknown_clip4.wav";
		}
		else if(i==50) {
			sound = sound+"victory.wav";
		}
		else if(i==51) {
			sound = sound+"adios_amigo.wav";
		}
		else if(i==52) {
			sound = sound+"good_morning.wav";
		}
		else if(i==53) {
			sound = sound+"bubble.wav";
		}
		else if(i==54) {
			sound = sound+"shuwa.wav";
		}
		else if(i==55) {
			sound = sound+"i_am_wooden_cube.wav";
		}

		writeMsg(myChan,user+": "+sound);
	}

	public void parseWorth(String line, String user, String ircName)
	{
		gameTime = System.currentTimeMillis();
		line = line.trim();
		int index = line.lastIndexOf(" ");
		String realUser = user;
		if(nickTranslation.containsKey(ircName)) {
			realUser = (String)nickTranslation.get(ircName);
		}
		if(index==-1 || line.substring(index).trim().equals(":WORTH")) {
			Pair p = (Pair)worthTable.get(realUser.toLowerCase());
			if(p==null) {
				p = new Pair("0","0");
			}
			long lastWorth = Long.valueOf(p.first().toString()).longValue();
			long totalWorth = Long.valueOf(p.second().toString()).longValue();
			long worthDiff = System.currentTimeMillis() - lastWorth;

			if(worthDiff > 86400000) {
				int cents = Math.abs(r.nextInt(90))+10;
				int dollars = Math.abs(r.nextInt(1000));
				totalWorth = totalWorth + (dollars*100) + cents;
				String worthString = totalWorth+"";
				worthString = worthString.substring(0,worthString.length()-2)+"."+worthString.substring(worthString.length()-2);
				writeMsg(myChan,BOLD+user+BOLD+" is worth $"+dollars+"."+cents+" today. "+BOLD+user+BOLD+" has a net worth of $"+worthString);

				Calendar c = Calendar.getInstance();
				if(c.get(c.DAY_OF_WEEK)!=7 && c.get(c.DAY_OF_WEEK)!=1) {
					synchronized(worthTable) {
						if(System.currentTimeMillis() - lastWorthAdjustment > 86400000) {
							if(r.nextInt(5)==0) {
								lastWorthAdjustment = System.currentTimeMillis();
								double adjustment = (double)((double)(r.nextInt(150))/(double)1000);
								boolean marketCrash = false;
								if(adjustment>0) {
									adjustment = adjustment * -1;
								}

								String quotesPage = loader.getURL("http://www.nasdaq.com/aspx/infoquotes.aspx?symbol=IXIC&selected=IXIC");
								try {
									index = quotesPage.indexOf("<label id='IXIC_Per2'>");
									String tmp = quotesPage.substring(index-150,index);
									quotesPage = quotesPage.substring(index + 22);
									index = quotesPage.toLowerCase().indexOf("%");
									quotesPage = quotesPage.substring(0,index);
									//									index = quotesPage.indexOf(">");
									try {
										adjustment = Double.valueOf(quotesPage.trim()).doubleValue();
										adjustment = adjustment / 100;
									}
									catch(Exception e) {
										System.out.println("Bad double value: "+quotesPage.substring(index+1).trim());
									}
									if(tmp.indexOf("images/redArrow")!=-1) {
										System.out.println("Markets are down, negating adjustment");
										if(adjustment>0) {
											adjustment = -1 * adjustment;
										}
										if(Math.abs(r.nextInt(37))==0) {
											adjustment = adjustment * 4.5;
											if(adjustment < -0.99) {
												adjustment = -0.99;
											}
											marketCrash = true;
											System.out.println("Market Crashed");
										}
									}
									else {
										System.out.println("Positive adjustment of: "+adjustment);
										if(adjustment<0) {
											adjustment = -1 * adjustment;
										}
										if(adjustment > 0.05) {
											adjustment = 0.05;
										}
									}
								}
								catch(Exception e) {
									System.out.println("Could not parse QUOTES page");
								}

								String adjustString = ""+(adjustment*100);
								index = adjustString.indexOf(".");
								if(index!=-1) {
									if(adjustString.length() - index >3) {
										adjustString = adjustString.substring(0,index+3);
									}
								}
								if(!marketCrash) {
									writeMsg(myChan,"However, due to the turbulent world market, the value of Chan Dollars have been adjusted by "+adjustString+"% to reflect the global market value.");
								}
								else {
									writeMsg(myChan,"However, due to all the retards in this channel, the value of Chan Dollars have crashed and requires an adjusted of "+adjustString+"%.");
								}
								totalWorth = totalWorth + (long)(totalWorth * adjustment);
								worthString = ""+totalWorth;
								worthString = worthString.substring(0,worthString.length()-2)+"."+worthString.substring(worthString.length()-2);
								writeMsg(myChan,"This across-the-board adjustment leaves "+BOLD+user+BOLD+" with a net worth of $"+worthString);

								Enumeration keys = worthTable.keys();
								while(keys.hasMoreElements()) {
									String worthUser = (String)keys.nextElement();
									if(!worthUser.equalsIgnoreCase(realUser)) {
										Pair p2 = (Pair)worthTable.get(worthUser);
										long worthUserWorth = Long.valueOf(p2.second().toString()).longValue();
										worthUserWorth = worthUserWorth + (long)(worthUserWorth * adjustment);
										p2 = new Pair(p2.first(),""+worthUserWorth);
										worthTable.put(worthUser,p2);
									}
								}
							}
						
						}
					}
				}
				p = new Pair(System.currentTimeMillis()+"",totalWorth+"");
				worthTable.put(realUser.toLowerCase(),p);
				saveStats();
			}
			else {
				System.out.println(System.currentTimeMillis()+" - lastWorth="+lastWorth+" worthTimeDiff="+worthDiff);
				String time = "";
				int timeleft = (int)(worthDiff);
				timeleft = (int)(timeleft/1000);
				int seconds = timeleft % 60;
				timeleft = (int)(timeleft/60);
				int minutes = timeleft % 60;
				timeleft = (int)(timeleft/60);
				if(timeleft>0) {
					if(timeleft==1) {
						time = time + timeleft + " hour, ";
					}
					else {
						time = time + timeleft + " hours, ";
					}
				}
				if(minutes > 0) {
					if(minutes==1) {
						time = time + minutes + " minute, ";
					}
					else {
						time = time + minutes + " minutes, ";
					}
				}
				if(seconds > 0) {
					if(seconds==1) {
						time = time + seconds + " second, ";
					}
					else {
						time = time + seconds + " seconds, ";
					}
				}
				writeMsg(user,"You asked for your self worth "+time+"ago. You need to wait longer before you can ask again");
			}
		}
		else {
			String askUser = line.substring(index).trim();
			String realAskUser = askUser;

			if(askUser.equalsIgnoreCase(myNick)) {
				writeMsg(myChan,BOLD+askUser+BOLD+" has a net worth of $"+r.nextInt());
			}
			else if(askUser.equalsIgnoreCase("boobs") || askUser.equalsIgnoreCase("tits") || askUser.equalsIgnoreCase("pantsu") || askUser.equalsIgnoreCase("panties")) {
				writeMsg(myChan,BOLD+askUser+BOLD+" is worth $"+Math.abs(r.nextInt())+""+Math.abs(r.nextInt())+""+Math.abs(r.nextInt()));
			}
			else if(askUser.length()>15) {
				writeMsg(myChan,BOLD+user+BOLD+" is retarded.");
				if(lastWorthless.equals(ircName)) {
					write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
					write("KICK "+myChan+" "+user+" :Please do mankind a favor and drown yourself");
				}
				else {
					lastWorthless = ircName;
				}
			}
			else if(askUser.equalsIgnoreCase("meatbag") || askUser.equalsIgnoreCase("miracles") || askUser.equalsIgnoreCase("meatbags") || askUser.equalsIgnoreCase("miracle") ||
					  askUser.equalsIgnoreCase("life") || askUser.equalsIgnoreCase("living") || askUser.equalsIgnoreCase("god") || askUser.equalsIgnoreCase("classes")) {
				writeMsg(myChan,BOLD+user+BOLD+" needs to become an hero, right now...");
				if(lastWorthless.equals(ircName)) {
					write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
					write("KICK "+myChan+" "+user+" :Please do mankind a favor and drown yourself");
				}
				else {
					lastWorthless = ircName;
				}
			}
			else if(askUser.equalsIgnoreCase("peanuts") || askUser.equalsIgnoreCase("love") || askUser.equalsIgnoreCase("moe") || askUser.equalsIgnoreCase("money") || 
					  askUser.equalsIgnoreCase("win") || askUser.equalsIgnoreCase("fail") || askUser.equalsIgnoreCase("sex") || askUser.equalsIgnoreCase("haruhi") || 
					  askUser.equalsIgnoreCase("you") || askUser.equalsIgnoreCase("you're") || askUser.equalsIgnoreCase("shit") || askUser.equalsIgnoreCase("usd") || 
					  askUser.equalsIgnoreCase("pr0n") || askUser.equalsIgnoreCase("porn") || askUser.equalsIgnoreCase("bombs") || askUser.equalsIgnoreCase("pantsu") || 
					  askUser.equalsIgnoreCase("being") || askUser.equalsIgnoreCase("america") || askUser.equalsIgnoreCase("japan") || askUser.equalsIgnoreCase("that") || 
					  askUser.equalsIgnoreCase("love") || askUser.equalsIgnoreCase("worth") || askUser.equalsIgnoreCase("loli") || askUser.equalsIgnoreCase("drugs") || 
					  askUser.equalsIgnoreCase("marijuana") || askUser.equalsIgnoreCase("pennies") || askUser.equalsIgnoreCase("war") || askUser.equalsIgnoreCase("tits")) {
				write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
				write("KICK "+myChan+" "+user+" :Please do mankind a favor and drown yourself");
			}
			else {
				Enumeration e = userList.keys();
				while(e.hasMoreElements()) {
					String username = (String)e.nextElement();
					String ircName2 = (String)userList.get(username);

					if(username.equalsIgnoreCase(askUser)) {
						if(nickTranslation.containsKey(ircName2)) {
							realAskUser = (String)nickTranslation.get(ircName2);
							break;
						}
					}
				}

				e = worthTable.keys();
				Vector v = new Vector();
				while(e.hasMoreElements()) {
					String u = (String)e.nextElement();
					Pair p = (Pair)worthTable.get(u);
					String worthString = (String)p.second();
					v.addElement(new Pair(worthString,u));
				}

				boolean sorted = false;
				Pair[] list = new Pair[v.size()];
				for(int x=0;x<list.length;x++) {
					list[x] = (Pair)v.elementAt(x);
				}
				while(!sorted) {
					sorted = true;
					for(int x=0;x<list.length-1;x++) {
						if(Long.valueOf((String)list[x].first()).longValue() < Long.valueOf((String)list[x+1].first()).longValue()) {
							Pair tmp = list[x];
							list[x] = list[x+1];
							list[x+1] = tmp;
							sorted = false;
						}
					}
				}

				int rank = 0;
				for(int x=0;x<list.length;x++) {
					Pair p = list[x];
					if(p.second().toString().equals(realAskUser.toLowerCase())) {
						rank = x + 1;
						break;
					}
				}
				
				Pair p = (Pair)worthTable.get(realAskUser.toLowerCase());
				if(p==null) {
					writeMsg(myChan,askUser+" is worthless.");
					if(user.equalsIgnoreCase("naveryw")) {
						write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
						write("KICK "+myChan+" "+user+" :Please do mankind a favor and drown yourself");
					}
					else {
						if(lastWorthless.equalsIgnoreCase(ircName)) {
							write("MODE "+myChan+" +b *!*"+ircName.substring(ircName.indexOf("@")));
							write("KICK "+myChan+" "+user+" :Please do mankind a favor and drown yourself");
						}
						else {
							lastWorthless = ircName;
						}
					}
				}
				else {
					String worthString = p.second().toString();
					worthString = worthString.substring(0,worthString.length()-2)+"."+worthString.substring(worthString.length()-2);
					writeMsg(myChan,BOLD+askUser+BOLD+" has a net worth of $"+worthString+", ranked #"+rank+" out of "+list.length+" people with any worth.");
				}
			}
		}
		gameTime = System.currentTimeMillis();
	}

	public void parseLoli(String line, String user, String ircName)
	{
		gameTime = System.currentTimeMillis();
		line = line.trim();
		int index = line.lastIndexOf(" ");
		String realUser = user;
		if(nickTranslation.containsKey(ircName)) {
			realUser = (String)nickTranslation.get(ircName);
		}
		if(index==-1 || line.substring(index).trim().equals(":LOLI")) {
			loliTable.tryLolis(realUser,user);
		}
		else {
			String askUser = line.substring(index).trim();
			int rank = -1;
			try {
				rank = Integer.parseInt(askUser);
			}
			catch(Exception e) {;}

			if(rank!=-1) {
				loliTable.askLolis(rank);
			}
			else {
				String realAskUser = askUser;
				Enumeration e = userList.keys();
				while(e.hasMoreElements()) {
					String username = (String)e.nextElement();
					String ircName2 = (String)userList.get(username);
					
					if(username.equalsIgnoreCase(askUser)) {
						if(nickTranslation.containsKey(ircName2)) {
							realAskUser = (String)nickTranslation.get(ircName2);
							break;
						}
					}
				}

				loliTable.askLolis(realAskUser,askUser);
			}
		}
		gameTime = System.currentTimeMillis();
	}


	public void parse8Ball(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			rollTime = System.currentTimeMillis();
			int num = Math.abs(r.nextInt(20));
			String msg = "";
			if(num==0) {
				msg = BOLD+"Signs point to yes."+BOLD;
			}
			else if(num==1) {
				msg = BOLD+"Yes."+BOLD;
			}
			else if(num==2) {
				msg = BOLD+"Reply hazy, try again."+BOLD;
			}
			else if(num==3) {
				msg = BOLD+"Without a doubt."+BOLD;
			}
			else if(num==4) {
				msg = BOLD+"My sources say no."+BOLD;
			}
			else if(num==5) {
				msg = BOLD+"As I see it, yes."+BOLD;
			}
			else if(num==6) {
				msg = BOLD+"You may rely on it."+BOLD;
			}
			else if(num==7) {
				msg = BOLD+"Concentrate and ask again."+BOLD;
			}
			else if(num==8) {
				msg = BOLD+"Outlook not so good."+BOLD;
			}
			else if(num==9) {
				msg = BOLD+"It is decidedly so."+BOLD;
			}
			else if(num==10) {
				msg = BOLD+"Better not tell you now."+BOLD;
			}
			else if(num==11) {
				msg = BOLD+"Very doubtful."+BOLD;
			}
			else if(num==12) {
				msg = BOLD+"Yes - definitely."+BOLD;
			}
			else if(num==13) {
				msg = BOLD+"It is certain."+BOLD;
			}
			else if(num==14) {
				msg = BOLD+"Cannot predict now."+BOLD;
			}
			else if(num==15) {
				msg = BOLD+"Most likely."+BOLD;
			}
			else if(num==16) {
				msg = BOLD+"Ask again later."+BOLD;
			}
			else if(num==17) {
				msg = BOLD+"My reply is no."+BOLD;
			}
			else if(num==18) {
				msg = BOLD+"Outlook good."+BOLD;
			}
			else if(num==19) {
				msg = BOLD+"Don't count on it. "+BOLD;
			}
			delay(1000,300);
			char c = (char)1;
			writeMsg(myChan,c+"ACTION shakes his Magic 8-Ball (tm) and peers inside"+c);
			writeMsg(myChan,user+": "+msg);
			updateStats(user,ircName,"8ball");
		}
	}

	public void parseRoll(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			String num = line.substring(line.indexOf("ROLL")+4).trim();
			try {
				rollTime = System.currentTimeMillis();
				if(num.length()==0) {
					num = "6";
				}
				if(num.indexOf("d")!=-1) {
					int numDie = Integer.parseInt(num.substring(0,num.indexOf("d")).trim());
					int numSide = Integer.parseInt(num.substring(num.indexOf("d")+1).trim());
					if(numSide<0) {
						writeMsg(myChan,user+" tries to roll dice with "+BOLD+numSide+BOLD+" sides and the universe implodes");
					}
					else if(numSide==0) {
						writeMsg(myChan,user+" tries to roll dice with no sides at all. What the "+UNDERLINE+"fuck"+UNDERLINE+" is wrong with him???");
					}
					else if(numDie>12) {
						writeMsg(myChan,user+" tries to roll "+BOLD+numDie+BOLD+" dice, which is more than any human can handle, and fails");
					}
					else if(numSide==1) {
						writeMsg(myChan,user+" tries to roll a bunch of 1-sided magical dice, "+UNDERLINE+user+" is retarded"+UNDERLINE);
					}
					else if(numDie<=0) {
						writeMsg(myChan,user+" tries to roll "+numDie+" dice, but was attacked by a mob of monkeys instead.");
					}
					else {
						StringBuffer out = new StringBuffer();
						out.append("PRIVMSG "+myChan+" :"+user+" rolls "+numDie+" "+BOLD+numSide+BOLD+"-sided magical");
						if(numDie == 1) {
							out.append(" die, and it landed on: ");
						}
						else {
							out.append(" dice, and they landed on: ");
						}
						long total = 0;
						for(int x=0;x<numDie;x++) {
							int roll = Math.abs(r.nextInt(numSide))+1;
							total = total + roll;
							out.append(roll+" ");
						}
						out.append("(total="+UNDERLINE+total+UNDERLINE+")");
						write(out.toString());
					}
				}
				else if(num.startsWith("-") || num.equals("0")) {
					delay(110,100);
					writeMsg(myChan,user+" tried to roll a "+BOLD+num+BOLD+" sided die but nothing happened");
				}
				else if(num.equals("1")) {
					delay(110,100);
					writeMsg(myChan,user+" rolls a "+BOLD+"1"+BOLD+"-sided magical die and it lands on: 1 (of course)");
				}
				else {
					int max = Integer.parseInt(num);
					int roll = Math.abs(r.nextInt(max)) + 1;					
					delay(110,100);
					writeMsg(myChan,user+" rolls a "+BOLD+max+BOLD+"-sided magical die and it lands on: "+roll);
				}
				updateStats(user,ircName,"roll");
			}
			catch(Exception e) {e.printStackTrace();}
		}
	}

	public void parseTrigger(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			String realUser = user;
			int random = Math.abs(r.nextInt(filterVector.size()));
			if(nickTranslation.containsKey(ircName)) {
				user = (String)nickTranslation.get(ircName);
			}
			try {
				if(!lastTriggerUser.equalsIgnoreCase(user)) {
					filterTime = System.currentTimeMillis();
					String filterResult = (String)filterVector.elementAt(random);
					while(filterResult.indexOf("+user")!=-1) {
						int index = filterResult.indexOf("+user");
						filterResult = filterResult.substring(0,index)+realUser+filterResult.substring(index+5);
					}
					while(filterResult.indexOf("BOLD")!=-1) {
						int index = filterResult.indexOf("BOLD");
						filterResult = filterResult.substring(0,index)+BOLD+filterResult.substring(index+4);
					}
					while(filterResult.indexOf("UNDERLINE")!=-1) {
						int index = filterResult.indexOf("UNDERLINE");
						filterResult = filterResult.substring(0,index)+UNDERLINE+filterResult.substring(index+9);
					}
					while(filterResult.indexOf("COLOR=RED")!=-1) {
						int index = filterResult.indexOf("COLOR=RED");
						filterResult = filterResult.substring(0,index)+RED+filterResult.substring(index+9);
					}
					while(filterResult.indexOf("COLOR=BLUE")!=-1) {
						int index = filterResult.indexOf("COLOR=BLUE");
						filterResult = filterResult.substring(0,index)+BLUE+filterResult.substring(index+10);
					}
					while(filterResult.indexOf("COLOR=GREEN")!=-1) {
						int index = filterResult.indexOf("COLOR=GREEN");
						filterResult = filterResult.substring(0,index)+GREEN+filterResult.substring(index+11);
					}
					while(filterResult.indexOf("COLOR=CYAN")!=-1) {
						int index = filterResult.indexOf("COLOR=CYAN");
						filterResult = filterResult.substring(0,index)+CYAN+filterResult.substring(index+10);
					}
					while(filterResult.indexOf("COLOR=PURPLE")!=-1) {
						int index = filterResult.indexOf("COLOR=PURPLE");
						filterResult = filterResult.substring(0,index)+PURPLE+filterResult.substring(index+12);
					}
					while(filterResult.indexOf("COLOR=YELLOW")!=-1) {
						int index = filterResult.indexOf("COLOR=YELLOW");
						filterResult = filterResult.substring(0,index)+filterResult.substring(index+12);
					}
					while(filterResult.indexOf("COLOR")!=-1) {
						int index = filterResult.indexOf("COLOR");
						filterResult = filterResult.substring(0,index)+END+filterResult.substring(index+5);
					}

					if(filterResult.startsWith("/me")) {
						char c = (char)1;
						filterResult = c+"ACTION"+filterResult.substring(3)+c;
					}
					writeMsg(myChan,filterResult);
					updateStats(user,ircName,"trigger");
					lastTriggerUser = user;
				}
			}
			catch(Exception e) {;}
		}
	}

	public void parseTrivia(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			triviaTime = System.currentTimeMillis();
			int index = getNextTriviaQuestionIndex();
			writeMsg(myChan,triviaQuestions.elementAt(index)+"  ('/msg "+myNick+" trivia' for the answer)");
			lastAnswer = (String)triviaAnswers.elementAt(index);
			updateStats(user,ircName,"trivia");
		}
	}

	public void parseEliza(String line, String user, String ircName)
	{
		Vector params = new Vector();
		Vector values = new Vector();

		try {
			int index = line.toLowerCase().indexOf(myNick.toLowerCase()+":");
			if(index!=-1) {
				line = line.substring(index + myNick.length()+1).trim();
				if(line.length()>0) {
					if(line.equalsIgnoreCase("scissors") || line.equalsIgnoreCase("scissor") || line.equalsIgnoreCase("rock") || line.equalsIgnoreCase("paper")) {
						if(System.currentTimeMillis()-rpsTime>rpsTimeout) {
							StringBuffer out = new StringBuffer();
							int myRPS = Math.abs(r.nextInt(9));
							int userRPS = 0;
							boolean win = false;
							boolean lose = false;
							String origUser = user;
							
							if(myRPS==0 || myRPS==3 || myRPS==6) {
								myRPS=-1;
							}
							else if(myRPS==1 || myRPS==4 || myRPS==7) {
								myRPS=0;
							}
							else {
								myRPS=1;
							}
							
							if(line.equalsIgnoreCase("scissor") || line.equalsIgnoreCase("scissors")) {
								userRPS = -1;
							}
							else if(line.equalsIgnoreCase("paper")) {
								userRPS = 1;
							}
							
							if(nickTranslation.containsKey(ircName)) {
								user = ((String)nickTranslation.get(ircName)).toLowerCase();
							}
							
							if(myRPS==0) {
								writeMsg(myChan,((char)1)+"ACTION shakes his fist violently, and when it stops his hand shows a "+BOLD+"Rock"+BOLD+((char)1));
							}
							else if(myRPS==1) {
								writeMsg(myChan,((char)1)+"ACTION shakes his fist violently, and when it stops his hand shows a "+BOLD+"Paper"+BOLD+((char)1));
							}
							else if(myRPS==-1) {
								writeMsg(myChan,((char)1)+"ACTION shakes his fist violently, and when it stops his hand shows a "+BOLD+"Scissor"+BOLD+((char)1));
							}
							
							if((myRPS==0 && userRPS==0) || (myRPS==1 && userRPS==1) || (myRPS==-1 && userRPS==-1)) {
								writeMsg(myChan,origUser+": The game is a draw");
							}
							else if(myRPS==0 && userRPS==-1) {
								writeMsg(myChan,origUser+": My "+BOLD+"Rock"+BOLD+" beats your "+BOLD+"Scissor"+BOLD+". I win");
								lose = true;
							}
							else if(myRPS==0 && userRPS==1) {
								writeMsg(myChan,origUser+": Your "+BOLD+"Paper"+BOLD+" beats my "+BOLD+"Rock"+BOLD+". You win");
								win = true;
							}
							else if(myRPS==-1 && userRPS==0) {
								writeMsg(myChan,origUser+": Your "+BOLD+"Rock"+BOLD+" beats my "+BOLD+"Scissor"+BOLD+". You win");
								win = true;
							}
							else if(myRPS==-1 && userRPS==1) {
								writeMsg(myChan,origUser+": My "+BOLD+"Scissor"+BOLD+" beats your "+BOLD+"Paper"+BOLD+". I win");
								lose = true;
							}
							else if(myRPS==1 && userRPS==0) {
								writeMsg(myChan,origUser+": My "+BOLD+"Paper"+BOLD+" beats your "+BOLD+"Rock"+BOLD+". I win");
								lose = true;
							}
							else if(myRPS==1 && userRPS==-1) {
								writeMsg(myChan,origUser+": Your "+BOLD+"Scissor"+BOLD+" beats my "+BOLD+"Paper"+BOLD+". You win");
								win = true;
							}
							
							if(lose) {
								Pair p = new Pair("0","0");
								if(rpsStats.containsKey(user)) {
									p = (Pair)rpsStats.get(user);
								}
								p.setSecond(""+(Integer.parseInt(p.second()+"")+1));
								rpsStats.put(user,p);
							}
							else if(win) {
								Pair p = new Pair("0","0");
								if(rpsStats.containsKey(user)) {
									p = (Pair)rpsStats.get(user);
								}
								p.setFirst(""+(Integer.parseInt(p.first()+"")+1));
								rpsStats.put(user,p);
								if(r.nextInt(20)==0) {
									writeMsg(myChan,"For your insight in Rock/Paper/Scissors, you win a voice");
									write("MODE "+myChan+" +v "+origUser);
								}
								else if(r.nextInt(10)==0) {
									writeMsg(origUser,"You have proved your Rock/Paper/Scissors prowess, you win a chance to add an internal message.");
									writeMsg(origUser,"/msg me with the message, you only get one chance, hurry before I forget.");
									randomAward = origUser;
								}
							}
							
							rpsTime = System.currentTimeMillis();
						}
						else {
							checkFailedTrigger(user,ircName);
						}
					}
					else if(line.equalsIgnoreCase("paste")) {
						writeMsg(myChan,user+": http://"+httpThread.httpHost+":"+httpThread.httpPort+"/paste");
					}
					else if(line.equalsIgnoreCase("utf-8")) {
						String randomUTF = "";
						int length = Math.abs(r.nextInt(20))+5;
						for(int x=0;x<length;x++) {
							randomUTF = randomUTF+unicode[Math.abs(r.nextInt(unicode.length))];
						}
						writeMsg(myChan,user+": "+randomUTF);
					}
					else {
						/*
						  params.addElement("Entry1");
						  values.addElement(line);
						  String page = loader.postURL("http://www-ai.ijs.si/eliza-cgi-bin/eliza_script",params,values);
						  if(page!=null && page.length()>100) {
						  index = page.lastIndexOf("<strong>Eliza:</strong>");
						  if(index!=-1) {
						  index = index + 23;
						  page = page.substring(index);
						  index = page.indexOf("<form");
						  if(index!=-1) {
						  page = page.substring(0,index).trim();
						  writeMsg(myChan,user+": "+page);
						  }
						  }
						  }
						*/
						ElizaMain eliza = null;
						synchronized(elizaVector) {
							for(int x=0;x<elizaVector.size();x++) {
								Pair p = (Pair)elizaVector.elementAt(x);
								if(p.first().toString().equals(ircName)) {
									eliza = (ElizaMain)p.second();
									elizaVector.removeElementAt(x);
									elizaVector.addElement(p);
									break;
								}
							}
							if(eliza==null) {
								eliza = new ElizaMain();
								eliza.readScript(true, "eliza_script");
								Pair p = new Pair(ircName,eliza);
								if(elizaVector.size()>10) {
									elizaVector.removeElementAt(0);
								}
								elizaVector.addElement(p);
							}
						}

						writeMsg(myChan,user+": "+eliza.processInput(line));
					}
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void parseYoutube(String line, String user, String ircName)
	{
		if(!isJavaClient(ircName)) {
			youtubeTime = System.currentTimeMillis();
			String page = null;
			int index = line.indexOf("YOUTUBE");
			try {
				if(index!=-1) {
					String random = null;
					line = line.substring(index+7).trim();
					if(line.length()==0) {
						ExecuteCommand command = new ExecuteCommand("./randomWord.sh");
						StringBuffer output = new StringBuffer();
						command.processOut = output;
						command.finishedWaitTime = (500 + r.nextInt(100));
						command.execute();
						line = output.toString().trim();
						random = line;
						System.out.println("RANDOM YOUTUBE: "+line);
					}
					line = HTMLStringTools.encode(line);
					page = loader.getURL("http://www.youtube.com/results?search_query="+line+"&search_type=");
					System.out.println("http://www.youtube.com/results?search_query="+line+"&search_type=");
					Vector videos = new Vector();
					index = page.indexOf("href=\"/watch?v=");
					if(index!=-1) {
						index = page.indexOf("href=\"/watch?v=",index+1);
					}
					while(index!=-1) {
						page = page.substring(index+6);
						index = page.indexOf("\"");
						String url = page.substring(0,index);
						if(url.indexOf("<")!=-1) {
							url = url.substring(0,url.indexOf("<"));
						}
						videos.addElement("http://www.youtube.com"+url);
						page = page.substring(index);
						index = page.indexOf("href=\"/watch?v=");
					}

					if(videos.size()>0) {
						index = Math.abs(r.nextInt(videos.size()));
						if(random!=null) {
							writeMsg(myChan,user+": ("+random+") "+videos.elementAt(index));
						}
						else {
							writeMsg(myChan,user+": "+videos.elementAt(index));
						}
					}
					else {
						writeMsg(myChan,"I can't find any youtube videos, you lose");
					}
					updateStats(user,ircName,"youtube");
				}
			}
			catch(Exception e) {e.printStackTrace();}
			youtubeTime = System.currentTimeMillis();
		}
	}

	public void parseKick(String line, String user, String ircName)
	{
		String kick = line.substring(line.indexOf(":KICK")+5).trim();
		if(userList.containsKey(kick.toLowerCase())) {
			if(!kickAttempts.contains(ircName)) {
				kickAttempts.addElement(ircName);
				int rand = r.nextInt(10);
				if(rand==0 || rand==1 || rand==2 || rand==3 || rand==4) {
					if(kick.toLowerCase().equals(myNick.toLowerCase()) || kick.toLowerCase().equals("brikhaus") || kick.toLowerCase().equals("zapalacx") || kick.toLowerCase().equals("reichu")) {
						writeMsg(myChan,user+" fires the kick cannon and it hits "+kick+" IN THE FACE, but somehow, he is unaffected.");
					}
					else {
						writeMsg(myChan,user+" fires the kick cannon and it hits "+kick+" IN THE FACE");
						write("KICK "+myChan+" "+kick+" :blame "+user+", don't blame me");
					}
				}
				else if(rand==9) {
					String newkick = null;
					while(newkick==null) {
						Enumeration e = userList.keys();
						while(e.hasMoreElements()) {
							String key = (String)e.nextElement();
							if(Math.abs(r.nextInt(userList.size()-1)) == 0) {
								if(!key.toLowerCase().equals(user.toLowerCase()) && !key.equals("ornette") && !key.equals(myNick) && !key.equals("jonlin")
									&& !key.toLowerCase().equals("brikhaus") && !key.toLowerCase().equals("zapalacx") && !key.toLowerCase().equals("reichu")
									) {
									newkick = key;
									break;
								}
							}
						}
					}
					writeMsg(myChan,user+" fires the kick cannon at "+kick+" but it misfires and hits "+newkick+" IN THE FACE INSTEAD");
					write("KICK "+myChan+" "+newkick+" :blame "+user+" for using the kick cannon, don't blame me");
				}
				else {
					writeMsg(myChan,user+" fires the kick cannon and it EXPLODES IN HIS FACE");
					write("KICK "+myChan+" "+user+" :nice try");
				}
			}
			else {
				writeMsg(myChan,user+" fires the kick cannon and it EXPLODES IN HIS FACE");
				write("KICK "+myChan+" "+user+" :nice try");
			}
		}
	}

	public void parseAnime(String line, String user, String ircName)
	{
		if(!isJavaClient(ircName)) {
			pantsuTime = System.currentTimeMillis();
			String page = null;
			try {
				String url = "http://www.google.com/m/search/?q="+HTMLStringTools.encode(line)+"+site%3Ahttp%3A%2F%2Fwww.animenewsnetwork.com%2Fencyclopedia%2Fanime.php";
				page = loader.getURL(url);

				if(page.length()>200) {
					int index = page.indexOf("u=http%3A%2F%2Fwww.animenewsnetwork.com");
					if(index==-1) {
						index = page.indexOf("u=http://www.animenewsnetwork.com");
					}
					if(index!=-1) {
						page = page.substring(index+2);
						index = page.indexOf("\"");
						page = page.substring(0,index);
						writeMsg(myChan,user+": "+HTMLStringTools.decode(page));
					}
				}
			}
			catch(Exception e) {e.printStackTrace();}
			pantsuTime = System.currentTimeMillis();
		}
	}

	public void parseIMDB(String line, String user, String ircName)
	{
		if(!isJavaClient(ircName)) {
			pantsuTime = System.currentTimeMillis();
			String page = null;
			try {
				String url = "http://www.imdb.com/find?s=all&q="+HTMLStringTools.encode(line);
				loader.setAutoRedirect(false);
				page = loader.getURL(url);
				loader.setAutoRedirect(true);
			
				if(loader.getResultCode().startsWith("302") || loader.getResultCode().startsWith("301")) {
					url = loader.getRedirectLocation();
					if(url.indexOf("?")!=-1) {
						url = url.substring(0,url.indexOf("?"));
					}
					writeMsg(myChan,user+": "+BOLD+line+BOLD+" "+url);
				}
				else if(page.length()>200) {
					int index = page.indexOf("Popular Titles");
					if(index==-1) {
						index = page.indexOf("Titles (Exact Matches)");
					}
					if(index==-1) {
						index = page.indexOf("Titles (Partial Matches)");
					}
					if(index==-1) {
						index = page.indexOf("Titles (Approx Matches)");
					}

					if(index!=-1) {
						index = page.indexOf(">1.</td>",index);
						if(index!=-1) {
							index = page.indexOf("<a href=\"",index);
							if(index!=-1) {
								page = page.substring(index+9);
								index = page.indexOf("\"");
								url = "http://www.imdb.com"+page.substring(0,index);
								index = page.indexOf(">");
								if(index!=-1) {
									page = page.substring(index+1);
									index = page.indexOf("</a>");
									String name = page.substring(0,index);
									page = page.substring(index+4);
									index = page.indexOf("<");
									name = name + page.substring(0,index);
									name = StringTools.removeHTMLTags(name).trim();
									writeMsg(myChan,user+": "+BOLD+name+BOLD+" "+url);
								}
								else {
									writeMsg(myChan,user+": "+url);
								}
							}
							else {
								writeMsg(myChan,user+": I think you are stupid.");
							}
						}
						else {
							writeMsg(myChan,user+": Can't seem to find your movie.");
						}
					}
					else {
						writeMsg(myChan,user+": Can't seem to find your movie.");
					}
				}
			}
			catch(Exception e) {e.printStackTrace();}
			pantsuTime = System.currentTimeMillis();
		}
	}

	public void parsePantsu(String line, String user, String ircName)
	{
		if(!isJavaClient(ircName)) {
			pantsuTime = System.currentTimeMillis();
			String page = null;
			try {
				int start = 0;
				int index = Math.abs(r.nextInt(5));
				if(index==1) {
					start = 21;
				}
				else if(index==2) {
					start = 42;
				}
				else if(index==3) {
					start = 63;
				}
				else if(index==4) {
					start = 84;
				}

				String url= "http://images.google.com/images?svnum=10&um=1&hl=en&q=victoria+secret&start="+start;
				index = Math.abs(r.nextInt(12));
				if(index==0 || index==1) {
					url = "http://images.google.co.jp/images?hl=ja&q=%E3%81%8A%E3%83%91%E3%83%B3%E3%83%84&ie=UTF-8&oe=UTF-8&um=1&sa=N&tab=wi&start="+start;
				}
				else if(index==2 || index==3) {
					url = "http://images.google.co.jp/images?svnum=10&um=1&hl=ja&q=%E4%B8%8B%E7%9D%80&btnG=%E3%82%A4%E3%83%A1%E3%83%BC%E3%82%B8%E6%A4%9C%E7%B4%A2&start="+start;
				}
				else if(index==4 || index==5) {
					url = "http://images.google.co.jp/images?svnum=10&um=1&hl=ja&q=%E3%81%B1%E3%82%93%E3%81%A1%E3%82%85&btnG=%E3%82%A4%E3%83%A1%E3%83%BC%E3%82%B8%E6%A4%9C%E7%B4%A2&start="+start;
				}
				else if(index==6 || index==7) {
					url = "http://images.google.co.jp/images?svnum=10&um=1&hl=ja&q=%E3%81%8A%E3%81%B1%E3%82%93%E3%81%A1%E3%82%85&btnG=%E3%82%A4%E3%83%A1%E3%83%BC%E3%82%B8%E6%A4%9C%E7%B4%A2&start="+start;
				}
				else if(index==8 || index==9) {
					url = "http://images.google.com/images?svnum=10&um=1&hl=en&q=panties&start="+start;
				}
				else if(index==10) {
					url= "http://images.google.com/images?svnum=10&um=1&hl=en&q=sloggi&start="+start;
				}

				page = loader.getURL(url);

				// dyn.Img("http://feel.g.hatena.ne.jp/keyword/%25E3%2583%2591%25E3%2583%25B3%25E3%2583%2584&h=473&w=477&sz=27&hl=ja&start=1&um=1","","Cj4NxKUV2494YM:","http://images-jp.amazon.com/images/P/B000BXY5KM.09.LZZZZZZZ.jpg","129","128","ALINCO \u30a8\u30af\u30b5\u30b5\u30a4\u30ba\x3cb\x3e\u30d1\u30f3\u30c4\x3c/b\x3eM EX-014","","","477W473 - 27k","jpg",

				// /imgres?imgurl=http://pub.ne.jp/selye/image/user/1140488196.jpg&imgrefurl=http://pub.ne.jp/selye/%3Fcat_id%3D12642&start=18&h=382&w=512&sz=40&tbnid=t4bglfugf6LSEM:&tbnh=98&tbnw=131&hl=ja&prev=/images%3Fq%3D%25E3%2581%258A%25E3%2583%2591%25E3%2583%25B3%25E3%2583%2584%26um%3D1%26hl%3Dja%26sa%3DN%26ie%3DUTF-8%26oe%3DUTF-8&um=1

				Vector images = new Vector();
				index = page.indexOf("/imgres?imgurl=http");
				while(index!=-1) {
					page = page.substring(index+3);
					index = page.indexOf("imgurl=http");
					if(index!=-1) {
						page = page.substring(index);
						index = page.indexOf("&");
						String imageURL = page.substring(7,index);
						index = imageURL.indexOf("%25");
						while(index!=-1) {
							imageURL = imageURL.substring(0,index) + "%" + imageURL.substring(index+3);
							index = imageURL.indexOf("%25");
						}
						images.addElement(imageURL);
					}
					else {
						break;
					}
					index = page.indexOf("/imgres?imgurl=http");
				}

				if(images.size()>0) {
					while(images.size()>0) {
						index = Math.abs(r.nextInt(images.size()));
						String pantsuURL = (String)images.elementAt(index);
						boolean blacklisted = false;
						for(int x=0;x<pantsuBlacklist.size();x++) {
							if(pantsuURL.indexOf(pantsuBlacklist.elementAt(x).toString())!=-1) {
								System.out.println("PANTSU: hit blacklist: "+pantsuBlacklist.elementAt(x));
								blacklisted = true;
								break;
							}
						}
						if(!blacklisted) {
							break;
						}
						else {
							images.removeElementAt(index);
						}
					}
					if(images.size()>0) {
						writeMsg(myChan,user+": "+images.elementAt(index));
					}
					else {
						writeMsg(myChan,user+" wanted some pantsu, but I couldn't deliver quality, try again");
					}
				}

				updateStats(user,ircName,"pantsu");
			}
			catch(Exception e) {e.printStackTrace();}
			pantsuTime = System.currentTimeMillis();
		}
	}

	public void parseBoobies(String line, String user, String ircName)
	{
		if(!isJavaClient(ircName)) {
			pantsuTime = System.currentTimeMillis();
			String page = null;
			try {
				int start = 0;
				int index = Math.abs(r.nextInt(8));
				if(index==1) {
					start = 21;
				}
				else if(index==2) {
					start = 42;
				}
				else if(index==3) {
					start = 63;
				}
				else if(index==4) {
					start = 84;
				}
				else if(index==5) {
					start = 105;
				}
				else if(index==6) {
					start = 126;
				}
				else if(index==7) {
					start = 147;
				}


				String url= "http://images.google.com/images?gbv=2&ndsp=21&hl=en&safe=off&q=boobies&start="+start;

				page = loader.getURL(url);

				// dyn.Img("http://feel.g.hatena.ne.jp/keyword/%25E3%2583%2591%25E3%2583%25B3%25E3%2583%2584&h=473&w=477&sz=27&hl=ja&start=1&um=1","","Cj4NxKUV2494YM:","http://images-jp.amazon.com/images/P/B000BXY5KM.09.LZZZZZZZ.jpg","129","128","ALINCO \u30a8\u30af\u30b5\u30b5\u30a4\u30ba\x3cb\x3e\u30d1\u30f3\u30c4\x3c/b\x3eM EX-014","","","477W473 - 27k","jpg",

				// /imgres?imgurl=http://pub.ne.jp/selye/image/user/1140488196.jpg&imgrefurl=http://pub.ne.jp/selye/%3Fcat_id%3D12642&start=18&h=382&w=512&sz=40&tbnid=t4bglfugf6LSEM:&tbnh=98&tbnw=131&hl=ja&prev=/images%3Fq%3D%25E3%2581%258A%25E3%2583%2591%25E3%2583%25B3%25E3%2583%2584%26um%3D1%26hl%3Dja%26sa%3DN%26ie%3DUTF-8%26oe%3DUTF-8&um=1

				Vector images = new Vector();
				index = page.indexOf("/imgres?imgurl=http");
				while(index!=-1) {
					page = page.substring(index+3);
					index = page.indexOf("imgurl=http");
					if(index!=-1) {
						page = page.substring(index);
						index = page.indexOf("&");
						String imageURL = page.substring(7,index);
						index = imageURL.indexOf("%25");
						while(index!=-1) {
							imageURL = imageURL.substring(0,index) + "%" + imageURL.substring(index+3);
							index = imageURL.indexOf("%25");
						}
						images.addElement(imageURL);
					}
					else {
						break;
					}
					index = page.indexOf("/imgres?imgurl=http");
				}

				if(images.size()>0) {
					while(images.size()>0) {
						index = Math.abs(r.nextInt(images.size()));
						String boobiesURL = (String)images.elementAt(index);
						boolean blacklisted = false;
						for(int x=0;x<boobiesBlacklist.size();x++) {
							if(boobiesURL.indexOf(boobiesBlacklist.elementAt(x).toString())!=-1) {
								System.out.println("boobies: hit blacklist: "+boobiesBlacklist.elementAt(x));
								blacklisted = true;
								break;
							}
						}
						if(!blacklisted) {
							break;
						}
						else {
							images.removeElementAt(index);
						}
					}
					if(images.size()>0) {
						writeMsg(myChan,user+": "+images.elementAt(index));
					}
					else {
						writeMsg(myChan,user+" wanted some boobies, but I couldn't deliver quality, try again");
					}
				}

				updateStats(user,ircName,"boobies");
			}
			catch(Exception e) {e.printStackTrace();}
			pantsuTime = System.currentTimeMillis();
		}
	}

	public void parseGame(String line,String user,String ircName)
	{
		if(!isJavaClient(ircName)) {
			gameTime = System.currentTimeMillis();
			String page = null;
			line = line.toLowerCase();
			if(line.indexOf("anagram")!=-1) {
				page = loader.getURL("http://www.gamesforthebrain.com/game/anagramania/");
			}
			else if(line.indexOf("hangman")!=-1) {
				page = loader.getURL("http://www.gamesforthebrain.com/game/letterama/");
			}
			else if(line.indexOf("word")!=-1) {
				page = loader.getURL("http://www.gamesforthebrain.com/game/whatword/");
			}
			else {
				writeMsg(myChan,"Try: GAME anagram, GAME hangman, or GAME word");
			}
			if(page!=null) {
				int index = page.indexOf("<p class=\"puzzle\">");
				if(index!=-1) {
					page = page.substring(index+18);
					index = page.indexOf("</p>");
					if(index!=-1) {
						page = StringTools.removeHTMLTags(page.substring(0,index).trim());
						writeMsg(myChan,page);
					}
				}
				updateStats(user,ircName,"game");
			}
		}
	}





	public void checkFailedTrigger(String user, String ircName)
	{
		String realUser = user;
		if(nickTranslation.containsKey(ircName)) {
			realUser = ((String)nickTranslation.get(ircName)).toLowerCase();
		}
		long thisTime = System.currentTimeMillis();
		synchronized(timeoutTriggers) {
			timeoutTriggers.addElement(new Pair(realUser,thisTime+""));
			if(timeoutTriggers.size()>10) {
				timeoutTriggers.removeElementAt(0);
			}
		}
		long lastTime = 0;
		int count = 0;
		synchronized(timeoutTriggers) {
			for(int x=timeoutTriggers.size()-1;x>=0;x--) {
				Pair p = (Pair)timeoutTriggers.elementAt(x);
				if(p.first().toString().equalsIgnoreCase(realUser)) {
					count++;
					if(count==3) {
						lastTime = Long.valueOf(p.second()+"").longValue();
					}
				}
			}
		}
		if(thisTime-lastTime < failedTriggerTimeout) {
			writeMsg(adminUser,"Added "+realUser+" to the shit list");
			shitList.addElement(realUser);
		}
	}


	public void processRSS(String release, String torrent)
	{
		System.out.println("PROCESS: "+release+" | "+torrent);
		if(release.indexOf("RE-ENCODE]")==-1) {
			synchronized(matchPatterns) {
				for(int x=0;x<matchPatterns.size();x++) {
					String[] patterns = (String[])matchPatterns.elementAt(x);
					String lrelease = release.toLowerCase();
					boolean match = true;
					for(int z=0;z<patterns.length;z++) {
						if(lrelease.indexOf(patterns[z])==-1) {
							match = false;
							break;
						}
					}
					if(match) {
						if(lastMatch!=x) {
							lastMatch = x;
							delay(10000,5000);
							writeMsg(myChan,BOLD+"THIS JUST IN: "+BOLD+release);
							writeMsg(myChan,torrent);
							idleTime = System.currentTimeMillis();
							break;
						}
					}
				}
			}
		}
	}


	public void googleTrends()
	{
		String page = loader.getURL("http://www.google.com/trends");
		int index = page.indexOf("<table class=hotTerm><tr><td class=num>&nbsp;&nbsp;1.</td>");
		if(index!=-1) {
			page = page.substring(index);
			index = page.indexOf("<a href=");
			page = page.substring(index);
			index = page.indexOf("/");
			page = page.substring(index);
			index = page.indexOf(">");
			String url = "http://www.google.com"+page.substring(0,index);
			page = page.substring(index+1);
			index = page.indexOf("<");
			String trend = page.substring(0,index);

			if(!lastTrend.equalsIgnoreCase(trend)) {
				writeMsg(myChan,"Did you know? The current hot trend is "+BOLD+trend+BOLD+" - "+url);
				lastTrend = trend;
				idleTime = System.currentTimeMillis();
			}
		}
	}


	protected boolean isJavaClient(String ircName)
	{
		return(ircName.startsWith("~Java-") || ircName.startsWith("Java-") || ircName.startsWith("JavaIRC") || ircName.startsWith("Rizon-Java") || ircName.indexOf(".mibbit.com")!=-1);
	}

	protected void printStats(String user, String recip)
	{
		StringBuffer out = new StringBuffer();
		if(recip == null) {
			recip = user;
		}
		out.append("PRIVMSG ");
		out.append(recip);
		out.append(" :You have /msg'ed me a total of ");
		if(msgStats.containsKey(user)) {
			out.append((String)msgStats.get(user));
		}
		else {
			out.append("zero");
		}
		out.append(" time(s). You have rolled ");
		if(rollStats.containsKey(user)) {
			out.append((String)rollStats.get(user));
			out.append(" die.");
		}
		else {
			out.append("no dice.");
		}
		out.append(" You have requested trivia ");
		if(triviaStats.containsKey(user)) {
			out.append((String)triviaStats.get(user));
		}
		else {
			out.append("zero");
		}
		out.append(" time(s) and asked for the answers ");
		if(answerStats.containsKey(user)) {
			out.append((String)answerStats.get(user));
		}
		else {
			out.append("zero");
		}
		out.append(" time(s).");
		delay(220,200);
		synchronized(out) {
			write(out.toString());
			out = new StringBuffer();
			out.append("PRIVMSG ");
			out.append(recip);
			out.append(" :You have requested ");
			if(gameStats.containsKey(user)) {
				out.append((String)gameStats.get(user));
			}
			else {
				out.append("no");
			}
			out.append(" game(s). You have requested ");
			if(fortuneStats.containsKey(user)) {
				out.append((String)fortuneStats.get(user));
			}
			else {
				out.append("no");
			}
			out.append(" fortune(s). You have searched through scripts ");
			if(scriptStats.containsKey(user)) {
				out.append((String)scriptStats.get(user));
			}
			else {
				out.append("zero");
			}
			out.append(" time(s).");
			delay(220,200);
			write(out.toString());
			out = new StringBuffer();
			out.append("PRIVMSG ");
			out.append(recip);
			out.append(" :You have added ");
			if(responsesStats.containsKey(user)) {
				out.append((String)responsesStats.get(user));
			}
			else {
				out.append("no");
			}
			out.append(" response(s). You have triggered ");
			if(triggerStats.containsKey(user)) {
				out.append((String)triggerStats.get(user));
			}
			else {
				out.append("zero");
			}
			out.append(" time(s). You have requested ");
			if(youtubeStats.containsKey(user)) {
				out.append((String)youtubeStats.get(user));
			}
			else {
				out.append("zero");
			}
			out.append(" youtube video(s). You have requested ");
			if(pantsuStats.containsKey(user)) {
				out.append((String)pantsuStats.get(user));
			}
			else {
				out.append("zero");
			}
			out.append(" pantsu images(s)");
			if(boobiesStats.containsKey(user)) {
				out.append((String)boobiesStats.get(user));
			}
			else {
				out.append("zero");
			}
			out.append(" boobies images(s)");
			delay(220,200);
			write(out.toString());

			if(rpsStats.containsKey(user)) {
				out = new StringBuffer();
				out.append("PRIVMSG ");
				out.append(recip);
				out.append(" :You have won ");
				Pair p = (Pair)rpsStats.get(user);
				out.append(p.first().toString());
				out.append(" and lost ");
				out.append(p.second().toString());
				out.append(" games of Rock/Paper/Scissors against me.");
				delay(220,200);
				write(out.toString());
			
			}
			
		}
	}

	public void pong(String line)
	{
		saveStats();
		saveFilters();

		int index = line.lastIndexOf(" :");
		delay(1000,500);
		if(index!=-1) {
			write("PONG "+myPong+" "+line.substring(index).trim());
		}
		if(mostInChannel < userList.size()) {
			mostInChannel = userList.size();
			lastMostInChannel = System.currentTimeMillis();
		}
		if(r.nextInt(30)==0) {
			delay(2000,1000);
			write("WHO "+myChan);
			userList = new Hashtable();
		}
		if(r.nextInt(30)==0) {
			loadALS();
		}
	}

	public void loadALS()
	{
		System.out.println("Reloading ALS list");
		try {
			String page = loader.getURL("http://www.tesuji.org/als_images/");
			Vector tmp = new Vector();
			int index = page.indexOf("<img src=");
			while(index!=-1) {
				page = page.substring(index);
				index = page.indexOf("<a href");
				if(index!=-1) {
					page = page.substring(index+9);
					index = page.indexOf("\"");
					if(index!=-1) {
						tmp.addElement("http://sente.tesuji.org/als_archive/"+page.substring(0,index));
					}
				}
				index = page.indexOf("<img src=");
			}

			if(tmp.size()>0) {
				alsImages = tmp;
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	public synchronized void updateUserList()
	{
		if(mostInChannel < userList.size()) {
			mostInChannel = userList.size();
			lastMostInChannel = System.currentTimeMillis();
		}

		Enumeration e = userList.keys();
		while(e.hasMoreElements()) {
			String username = (String)e.nextElement();
			if(!username.equalsIgnoreCase(myNick)) {
				if(!idleStats.containsKey(username)) {
					idleStats.put(username,""+System.currentTimeMillis());
				}
			}
		}
		Vector removeThese = new Vector();
		for(int x=0;x<recentVoice.size();x++) {
			String username = (String)recentVoice.elementAt(x);
			if(!userList.containsKey(username)) {
				removeThese.addElement(username);
			}
		}
		for(int x=0;x<removeThese.size();x++) {
			String username = (String)removeThese.elementAt(x);
			recentVoice.remove(username);
		}
		e = idleStats.keys();
		while(e.hasMoreElements()) {
			String username = (String)e.nextElement();
			if(!userList.containsKey(username)) {
				idleStats.remove(username);
			}
			else if(!username.equals("jonlin") && !username.equals("ornette")){
				long idle = Long.valueOf((String)idleStats.get(username)).longValue();
				int timestmp = (int)(System.currentTimeMillis() - idle);
				if(System.currentTimeMillis() - timestamp > 300000) {
					timestamp = System.currentTimeMillis();

					if(timestmp > voiceTimeout && !recentVoice.contains(username) &&  r.nextInt(10)==0) {
						StringBuffer idleTime = new StringBuffer();
						
						timestmp = timestmp/1000;
						int seconds = timestmp % 60;
						timestmp = timestmp / 60;
						int minutes = timestmp % 60;
						timestmp = timestmp / 60;
						int hours = timestmp % 24;
						timestmp = timestmp / 24;
						
						if(timestmp>0) {
							idleTime.append(timestmp+" days, ");
						}
						if(hours>0) {
							idleTime.append(hours+" hours, ");
						}
						if(minutes>0) {
							idleTime.append(minutes+" minutes, ");
						}
						idleTime.append(seconds+" seconds");
						writeMsg(myChan,"Congratulations "+BOLD+username.toUpperCase()+BOLD+", you've won a voice for idling in "+myChan+"! You have idled for "+idleTime);
						write("MODE "+myChan+" +v "+username);
						recentVoice.addElement(username);
					}
				}
			}
		}
	}


	public synchronized void loadFilters()
	{
		File f = new File(filterFile);
		if(f.exists()) {
			try {
				//				BufferedReader fin = new BufferedReader(new FileReader(f));
				FileInputStream fin = new FileInputStream(f);
				String line = readLine(fin);
				if(line!=null && line.trim().length()>0) {
					filterWord = line;
				}
				line = readLine(fin);
				while(line!=null) {
					if(line.trim().length()>0) {
						filterVector.addElement(line);
						filterVector.removeElementAt(0);
					}
					line = readLine(fin);
				}
				fin.close();
			}
			catch(Exception e) {e.printStackTrace();}
		}
	}

	public synchronized void saveFilters()
	{
		File f = new File(filterFile);
		try {
			//			FileWriter fout = new FileWriter(f);
			FileOutputStream fout = new FileOutputStream(f);
			//			fout.write(filterWord+"\n");
			fout.write(filterWord.getBytes("UTF-8"));
			fout.write(NEWLINE);
			for(int x=0;x<10;x++) {
				//				fout.write(filterVector.elementAt(x).toString());
				//				fout.write("\n");
				fout.write(filterVector.elementAt(x).toString().getBytes("UTF-8"));
				fout.write(NEWLINE);
			}
			fout.flush();
			fout.close();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	protected synchronized void readStats()
	{
		try {
			File f = new File("script.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					scriptStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("trigger.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					triggerStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("responses.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					responsesStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("fortune.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					fortuneStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("fortuneDatabase.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					fortuneDatabaseStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("game.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					gameStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("rps.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf(" ");
				if(index!=-1) {
					String user = line.substring(0,index).trim();
					line = line.substring(index).trim();
					index = line.indexOf(" ");
					if(index!=-1) {
						Pair p = new Pair(line.substring(0,index).trim(),line.substring(index).trim());
						rpsStats.put(user,p);
					}
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {;};
		try {
			File f = new File("trivia.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					triviaStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("answer.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					answerStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("roll.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					rollStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("8ball.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					ballStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("youtube.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					youtubeStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("pantsu.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					pantsuStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("boobies.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					boobiesStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("msg.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					msgStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channel.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			if(line!=null) {
				channelStatsDate = line;
				line = fin.readLine();
			}
			if(line!=null) {
				dotStats = Integer.parseInt(line.trim());
				line = fin.readLine();
			}
			if(line!=null) {
				lastMostInChannel = Long.valueOf(line.trim()).longValue();
				line = fin.readLine();
			}
			if(line!=null) {
				mostInChannel = Integer.parseInt(line.trim());
				line = fin.readLine();
			}
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelCaps.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelCapsStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelLinks.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelLinksStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelShort.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelShortStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelLong.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelLongStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelQuestion.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelQuestionStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelJoin.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelJoinStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("channelEmoticon.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					channelEmoticonStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("idle.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf("=");
				if(index!=-1) {
					idleStats.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
		try {
			File f = new File("nickTranslation.stats");
			BufferedReader fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.lastIndexOf("=");
				if(index!=-1) {
					nickTranslation.nicks.addElement(new Pair(line.substring(index+1).trim(),line.substring(0,index).trim()));
					//					nickTranslation.put(line.substring(0,index).trim(),line.substring(index+1).trim());
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {;}
	}

	protected synchronized boolean saveStats()
	{
		boolean rval = false;
		try {
			File f = new File("script.stats");
			FileWriter fout = new FileWriter(f);
			Enumeration e = scriptStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)scriptStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("trigger.stats");
			fout = new FileWriter(f);
			e = triggerStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)triggerStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("responses.stats");
			fout = new FileWriter(f);
			e = responsesStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)responsesStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("fortune.stats");
			fout = new FileWriter(f);
			e = fortuneStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)fortuneStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("fortuneDatabase.stats");
			fout = new FileWriter(f);
			e = fortuneDatabaseStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)fortuneDatabaseStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("game.stats");
			fout = new FileWriter(f);
			e = gameStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)gameStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("rps.stats");
			fout = new FileWriter(f);
			e = rpsStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				Pair p = (Pair)rpsStats.get(user);
				fout.write(user+" "+p.first()+" "+p.second()+"\n");
			}
			fout.close();

			f = new File("trivia.stats");
			fout = new FileWriter(f);
			e = triviaStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)triviaStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("answer.stats");
			fout = new FileWriter(f);
			e = answerStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)answerStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("roll.stats");
			fout = new FileWriter(f);
			e = rollStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)rollStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("8ball.stats");
			fout = new FileWriter(f);
			e = ballStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)ballStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("youtube.stats");
			fout = new FileWriter(f);
			e = youtubeStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)youtubeStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("pantsu.stats");
			fout = new FileWriter(f);
			e = pantsuStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)pantsuStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("boobies.stats");
			fout = new FileWriter(f);
			e = boobiesStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)boobiesStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("msg.stats");
			fout = new FileWriter(f);
			e = msgStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)msgStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channel.stats");
			fout = new FileWriter(f);
			e = channelStats.keys();
			fout.write(channelStatsDate+"\n");
			fout.write(dotStats+"\n");
			fout.write(lastMostInChannel+"\n");
			fout.write(mostInChannel+"\n");
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelCaps.stats");
			fout = new FileWriter(f);
			e = channelCapsStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelCapsStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelLinks.stats");
			fout = new FileWriter(f);
			e = channelLinksStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelLinksStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelShort.stats");
			fout = new FileWriter(f);
			e = channelShortStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelShortStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelLong.stats");
			fout = new FileWriter(f);
			e = channelLongStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelLongStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelQuestion.stats");
			fout = new FileWriter(f);
			e = channelQuestionStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelQuestionStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelJoin.stats");
			fout = new FileWriter(f);
			e = channelJoinStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelJoinStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("channelEmoticon.stats");
			fout = new FileWriter(f);
			e = channelEmoticonStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)channelEmoticonStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("idle.stats");
			fout = new FileWriter(f);
			e = idleStats.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				String stat = (String)idleStats.get(user);
				fout.write(user+"="+stat+"\n");
			}
			fout.close();

			f = new File("nickTranslation.stats");
			fout = new FileWriter(f);
			for(int x=0;x<nickTranslation.nicks.size();x++) {
				Pair p = (Pair)nickTranslation.nicks.elementAt(x);
				fout.write(p.second()+"="+p.first()+"\n");
			}
			fout.close();

			f = new File("worth.stats");
			fout = new FileWriter(f);
			e = worthTable.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				Pair p = (Pair)worthTable.get(user);
				fout.write(user+"="+p.first()+"|"+p.second()+"\n");
			}
			fout.close();

			rval = true;
		}
		catch(Exception e) {e.printStackTrace();}

		loliTable.writeToFile();
		return(rval);
	}

	protected synchronized void loadSettings()
	{
		File f = new File("settings.txt");
		BufferedReader fin = null;
		try {
			fin = new BufferedReader(new FileReader(f));
			randomTimeout = (long)Integer.parseInt(fin.readLine().trim());
			fortuneTimeout = (long)Integer.parseInt(fin.readLine().trim());
			gameTimeout = (long)Integer.parseInt(fin.readLine().trim());
			triviaTimeout1 = (long)Integer.parseInt(fin.readLine().trim());
			triviaTimeout2 = (long)Integer.parseInt(fin.readLine().trim());
			filterTimeout = (long)Integer.parseInt(fin.readLine().trim());
			statsTimeout = (long)Integer.parseInt(fin.readLine().trim());
			idleTimeout = (long)Integer.parseInt(fin.readLine().trim());
			floodTimeout = (long)Integer.parseInt(fin.readLine().trim());
			rollTimeout = (long)Integer.parseInt(fin.readLine().trim());
			autojoinTimeout = Integer.parseInt(fin.readLine().trim());
			nickTimeout = Integer.parseInt(fin.readLine().trim());
			analNickTimeout = Integer.parseInt(fin.readLine().trim());
			lastFloodUser = fin.readLine().trim();
			floodLines = Integer.parseInt(fin.readLine().trim());
			hFloodLines = Integer.parseInt(fin.readLine().trim());
			hFloodLength = Integer.parseInt(fin.readLine().trim());
			floodMultiplier = Integer.parseInt(fin.readLine().trim());
			voiceTimeout = Integer.parseInt(fin.readLine().trim());
			pornTimeout = (long)Integer.parseInt(fin.readLine().trim());
			loliTimeout = Integer.parseInt(fin.readLine().trim());
			trendTimeout = Integer.parseInt(fin.readLine().trim());
			greetingProbability = Integer.parseInt(fin.readLine().trim());
			loliKillBase = Integer.parseInt(fin.readLine().trim());
			loliKillRoll = Integer.parseInt(fin.readLine().trim());
			String line = fin.readLine();
			while(line!=null) {
				if(!shitList.contains(line)) {
					shitList.addElement(line);
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fin.close();
			}
			catch(Exception e) {;}
		}

		f = new File("last.txt");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			lastTriggerUser = readLine(fis).trim();
			lastResponseUser = readLine(fis).trim();
			lastCaps = readLine(fis).trim();
			lastLinks = readLine(fis).trim();
			lastShort = readLine(fis).trim();
			lastLong = readLine(fis).trim();
			lastQuestion = readLine(fis).trim();
			lastJoin = readLine(fis).trim();
			lastEmoticon = readLine(fis).trim();

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fis.close();
			}
			catch(Exception e) {;}
		}

		pantsuBlacklist = new Vector();
		f = new File("pantsuBlacklist.txt");
		fin = null;
		try {
			fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				if(line.trim().length()>0) {
					pantsuBlacklist.addElement(line);
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fin.close();
			}
			catch(Exception e) {;}
		}

		boobiesBlacklist = new Vector();
		f = new File("boobiesBlacklist.txt");
		fin = null;
		try {
			fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				if(line.trim().length()>0) {
					boobiesBlacklist.addElement(line);
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fin.close();
			}
			catch(Exception e) {;}
		}

		matchPatterns = new Vector();
		f = new File("toshoPatterns.txt");
		fin = null;
		try {
			fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				if(line.trim().length()>0) {
					matchPatterns.addElement(StringTools.commaToArray(line.trim()));
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {e.printStackTrace();}
		finally{
			try {
				fin.close();
			}
			catch(Exception e) {;}
		}

		worthTable = new Hashtable();
		f = new File("worth.stats");
		fin = null;
		try {
			fin = new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				if(line.trim().length()>0) {
					int index = line.indexOf("=");
					String user = line.substring(0,index);
					line = line.substring(index+1);
					index = line.indexOf("|");
					Pair p = new Pair(line.substring(0,index),line.substring(index+1).trim());
					worthTable.put(user,p);
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {e.printStackTrace();}
		finally{
			try {
				fin.close();
			}
			catch(Exception e) {;}
		}

		loliTable.readFromFile();


		fin = null;
		try {
			recentFilters = new Hashtable();
			fin = new BufferedReader(new FileReader("recentFilters.txt"));
			String line = fin.readLine();
			while(line!=null) {
				int index = line.indexOf(" ");
				if(index!=-1) {
					String ip = line.substring(0,index).trim();
					line = line.substring(index).trim();
					index = line.indexOf("=");
					if(index!=-1) {
						if(recentFilters.containsKey(ip)) {
							Vector filters = (Vector)recentFilters.get(ip);
							filters.addElement(new Pair(line.substring(0,index).trim(),line.substring(index+1).trim()));
						}
						else {
							Vector filters = new Vector();
							filters.addElement(new Pair(line.substring(0,index).trim(),line.substring(index+1).trim()));
							recentFilters.put(ip,filters);
						}
					}
					else {
						index = line.indexOf("~");
						if(index!=-1) {
							if(recentFilters.containsKey(ip)) {
								Vector filters = (Vector)recentFilters.get(ip);
								filters.addElement(new Pair("~"+line.substring(0,index).trim(),line.substring(index+1).trim()));
							}
							else {
								Vector filters = new Vector();
								filters.addElement(new Pair("~"+line.substring(0,index).trim(),line.substring(index+1).trim()));
								recentFilters.put(ip,filters);
							}
						}
						else {
							index = line.indexOf("@");
							if(recentFilters.containsKey(ip)) {
								Vector filters = (Vector)recentFilters.get(ip);
								filters.addElement(new Pair("@"+line.substring(0,index).trim(),line.substring(index+1).trim()));
							}
							else {
								Vector filters = new Vector();
								filters.addElement(new Pair("@"+line.substring(0,index).trim(),line.substring(index+1).trim()));
								recentFilters.put(ip,filters);
							}
						}
					}
				}
				line = fin.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {fin.close();}catch(Exception e) {;}
		}

		/*
		  fin = null;
		  try {
		  fin = new BufferedReader(new FileReader(""));
		  String line = fin.readLine();
		  String question = null;
		  while(line!=null) {
		  if(question==null && line.indexOf("<Trivia>")!=-1) {
		  int index = line.indexOf("<Trivia>")+9;
		  line = line.substring(index);
		  try {
		  if(Character.isDigit(line.charAt(0))) {
		  while(Character.isDigit(line.charAt(0))) {
		  line = line.substring(1);
		  }
		  if(line.charAt(0)=='.') {
		  question = line.substring(1).trim();
		  }							
		  }
		  }
		  catch(Exception e) {;}
		  }
		  else if(question!=null && line.indexOf("<Trivia>")!=-1) {
		  if(line.indexOf("Time's up! The answer was:")!=-1) {
		  //						System.out.println("Question = "+question);
		  triviaBotAnswers.put(question,line.substring(line.indexOf("Time's up! The answer was:")+26).trim());
		  question = null;
		  }
		  }
		  line = fin.readLine();
		  }
		  }
		  catch(Exception e) {
		  e.printStackTrace();
		  }
		  finally {
		  try {fin.close();}catch(Exception e) {;}
		  }
		  System.out.println("Read: "+triviaBotAnswers.size()+" questions/answers");
		*/
	}

	protected synchronized void saveSettings()
	{
		File f = new File("settings.txt");
		FileWriter fout = null;
		try {
			fout = new FileWriter(f);
			fout.write(randomTimeout+"\n");
			fout.write(fortuneTimeout+"\n");
			fout.write(gameTimeout+"\n");
			fout.write(triviaTimeout1+"\n");
			fout.write(triviaTimeout2+"\n");
			fout.write(filterTimeout+"\n");
			fout.write(statsTimeout+"\n");
			fout.write(idleTimeout+"\n");
			fout.write(floodTimeout+"\n");
			fout.write(rollTimeout+"\n");
			fout.write(autojoinTimeout+"\n");
			fout.write(nickTimeout+"\n");
			fout.write(analNickTimeout+"\n");
			fout.write(lastFloodUser+"\n");
			fout.write(floodLines+"\n");
			fout.write(hFloodLines+"\n");
			fout.write(hFloodLength+"\n");
			fout.write(floodMultiplier+"\n");
			fout.write(voiceTimeout+"\n");
			fout.write(pornTimeout+"\n");
			fout.write(loliTimeout+"\n");
			fout.write(trendTimeout+"\n");
			fout.write(greetingProbability+"\n");
			fout.write(loliKillBase+"\n");
			fout.write(loliKillRoll+"\n");
			for(int x=0;x<shitList.size();x++) {
				fout.write(shitList.elementAt(x)+"\n");
			}
			fout.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fout.close();
			}
			catch(Exception e) {;}
		}

		f = new File("last.txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(lastTriggerUser.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastResponseUser.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastCaps.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastLinks.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastShort.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastLong.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastQuestion.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastJoin.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.write(lastEmoticon.getBytes("UTF-8"));
			fos.write(NEWLINE);
			fos.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fos.close();
			}
			catch(Exception e) {;}
		}
	}


	protected synchronized void combineStats(String line, String user1, String user2)
	{
		Pair p = (Pair)worthTable.get(user1);
		if(p!=null) {
			worthTable.remove(user1);
			Pair p2 = (Pair)worthTable.get(user2);
			if(p2==null) {
				worthTable.put(user2,p);
			}
			else {
				long worth1 = Long.valueOf(p.second().toString()).longValue();
				long worth2 = Long.valueOf(p2.second().toString()).longValue();
				p2 = new Pair(p2.first(),""+(worth1+worth2));
				worthTable.put(user2,p2);
			}
		}
		line = (String)scriptStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)scriptStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				scriptStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)triggerStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)triggerStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				triggerStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)responsesStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)responsesStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				responsesStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)fortuneStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)fortuneStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				fortuneStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)gameStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)gameStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				gameStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		p = (Pair)rpsStats.remove(user1);
		if(p!=null) {
			try {
				int count1 = Integer.parseInt(p.first()+"");
				int count2 = Integer.parseInt(p.second()+"");
				Pair p2 = (Pair)rpsStats.get(user2);
				if(p2!=null) {
					p.setFirst(count1+Integer.parseInt(p2.first()+"")+"");
					p.setSecond(count2+Integer.parseInt(p2.second()+"")+"");
				}
				rpsStats.put(user2,p);
			}
			catch(Exception e) {;}
		}
		line = (String)triviaStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)triviaStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				triviaStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)answerStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)answerStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				answerStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)rollStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)rollStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				rollStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)ballStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)ballStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				ballStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)youtubeStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)youtubeStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				youtubeStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)pantsuStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)pantsuStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				pantsuStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)boobiesStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)boobiesStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				boobiesStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)msgStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)msgStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				msgStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelCapsStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelCapsStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelCapsStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelLinksStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelLinksStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelLinksStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelShortStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelShortStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelShortStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelLongStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelLongStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelLongStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelQuestionStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelQuestionStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelQuestionStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelJoinStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelJoinStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelJoinStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}
		line = (String)channelEmoticonStats.remove(user1);
		if(line!=null) {
			try {
				int count1 = Integer.parseInt(line);
				line = (String)channelEmoticonStats.get(user2);
				if(line!=null) {
					count1 = count1 + Integer.parseInt(line);
				}
				channelEmoticonStats.put(user2,count1+"");
			}
			catch(Exception e) {;}
		}

		loliTable.combineUsers(user1,user2);

		/*
		  synchronized(nickTranslation) {
		  Enumeration e = nickTranslation.keys();
		  while(e.hasMoreElements()) {
		  String ircName = (String)e.nextElement();
		  if(user1.equalsIgnoreCase((String)nickTranslation.get(ircName))) {
		  System.out.println("Adding to nickTranslation: "+ircName+"="+user2);
		  nickTranslation.put(ircName,user2);
		  }
		  }
		  }
		*/
	}

	protected synchronized void updateStats(String user, String ircName, String stat)
	{
		if(!isJavaClient(ircName)) {
			Hashtable hash = null;
			if(stat.equals("trigger")) {
				hash = triggerStats;
			}
			else if(stat.equals("responses")) {
				hash = responsesStats;
			}
			else if(stat.equals("fortune")) {
				hash = fortuneStats;
			}
			else if(stat.equals("script")) {
				hash = scriptStats;
			}
			else if(stat.equals("game")) {
				hash = gameStats;
			}
			else if(stat.equals("trivia")) {
				hash = triviaStats;
			}
			else if(stat.equals("answer")) {
				hash = answerStats;
			}
			else if(stat.equals("roll")) {
				hash = rollStats;
			}
			else if(stat.equals("8ball")) {
				hash = ballStats;
			}
			else if(stat.equals("youtube")) {
				hash = youtubeStats;
			}
			else if(stat.equals("pantsu")) {
				hash = pantsuStats;
			}
			else if(stat.equals("boobies")) {
				hash = boobiesStats;
			}
			else if(stat.equals("msg")) {
				hash = msgStats;
			}
			else if(stat.equals("channel")) {
				hash = channelStats;
			}
			else if(stat.equals("channelCaps")) {
				hash = channelCapsStats;
			}
			else if(stat.equals("channelLinks")) {
				hash = channelLinksStats;
			}
			else if(stat.equals("channelShort")) {
				hash = channelShortStats;
			}
			else if(stat.equals("channelLong")) {
				hash = channelLongStats;
			}
			else if(stat.equals("channelQuestion")) {
				hash = channelQuestionStats;
			}
			else if(stat.equals("channelJoin")) {
				hash = channelJoinStats;
			}
			else if(stat.equals("channelEmoticon")) {
				hash = channelEmoticonStats;
			}
			if(nickTranslation.containsKey(ircName)) {
				user = (String)nickTranslation.get(ircName);
			}
			else if(ircName.trim().length()>0) {
				System.out.println("Adding to nickTranslation: "+ircName+"="+user);
				nickTranslation.put(ircName,user);
			}			
			user = user.toLowerCase();
			if(idleStats.containsKey(user)) {
				idleStats.remove(user);
			}
			if(hash!=null) {
				if(!hash.containsKey(user)) {
					hash.put(user,"0");
				}
				try {
					int count = Integer.parseInt((String)hash.get(user));
					count++;
					hash.put(user,""+count);
				}
				catch(Exception e) {;}
			}
		}
	}

	protected String getIRCName(String input)
	{
		String rval = "";
		int index = input.indexOf("!");
		if(index!=-1) {
			input = input.substring(index+1);
			index = input.indexOf(" ");
			if(index!=-1) {
				rval = input.substring(0,index);
			}
		}
		return(rval);
	}

	protected String getUser(String input)
	{
		String rval = "";
		int index = input.indexOf("!");
		if(index!=-1) {
			rval = input.substring(1,index);
		}
		return(rval);
	}

	protected void delay(int ms, int var)
	{
		try {
			if(var>ms) {
				var = ms-1;
			}
			runThread.sleep(ms + r.nextInt(var));
		}
		catch(Exception e) {;}
	}

	protected void write(String s)
	{
		if(s.indexOf(":")!=s.length()-1) {
			try {
				System.out.println("SENDING: "+s);
				out.write((s+"\r\n").getBytes("UTF-8"));
				out.flush();
			}
			catch(Exception e) {e.printStackTrace();}
			if(s.indexOf("PRIVMSG "+myChan+" :")!=-1) {
				addRecent(myNick,s.substring(s.indexOf("PRIVMSG "+myChan+" :")+("PRIVMSG "+myChan+" :").length()),"",false);
			}
		}
		else {
			System.out.println("SENDING: (NOTHING)");
		}
	}

	protected void writeMsg(String target, String message)
	{
		try {
			byte[] targetBytes = target.getBytes("UTF-8");
			byte[] messageBytes = message.getBytes("UTF-8");
			out.write(PRIVMSG);
			out.write(SPACE);
			out.write(targetBytes);
			out.write(SPACE);
			out.write(COLON);
			out.write(messageBytes);
			out.write(RETURN);
			out.flush();

			/*
			  for(int x=0;x<messageBytes.length;x++) {
			  int c = (int)messageBytes[x];
			  if(c<16) {
			  System.out.print(" 0");
			  }
			  else {
			  System.out.print(" ");
			  }
			  System.out.print(Integer.toHexString(c));
			  }
			*/
			System.out.println("SENDING: PRIVMSG "+target+" :"+message);
		}
		catch(Exception e) {e.printStackTrace();}
		if(target.equalsIgnoreCase(myChan)) {
			addRecent(myNick,message,"",false);
		}
	}

	protected void claimNick()
	{
		if(password!=null) {
			System.out.println("##### CLAIMING NICK");
			if(needGhost) {
				writeMsg("NickServ","GHOST "+myNick+" "+password);
			}
			if(needRelease) {
				writeMsg("NickServ","RELEASE "+myNick+" "+password);
				needRelease = false;
			}
			delay(5000,0);
			write("NICK "+myNick);
			writeMsg("NickServ","IDENTIFY "+password);
		}
		else {
			write("NICK "+myNick);
		}
	}

	protected boolean connect()
	{
		boolean rval = false;
		try {
			if(ircSock==null) {
				ircSock = new Socket(ircServer,ircPort);
				ircSock.setSoTimeout(600000);
				//				in = new BufferedReader(new InputStreamReader(ircSock.getInputStream()));
				//				in = new PushbackInputStream(ircSock.getInputStream(),4096);
				in = ircSock.getInputStream();
				out = ircSock.getOutputStream();
				rval = true;
				System.out.println("Connected to: "+ircServer+":"+ircPort);
			}
			else {
				System.out.println("Seems we're already connected");
			}
		}
		catch(Exception e) {e.printStackTrace();}
		return(rval);
	}

	public String encode(String message)
	{
		StringBuffer rval = new StringBuffer();
		for(int x=0;x<message.length();x++) {
			char c = message.charAt(x);
			if(c=='&') {
				try {
					if(message.charAt(x+1)=='#' && Character.isDigit(message.charAt(x+2))) {
						rval.append(c);
					}
					else {
						rval.append("&amp;");
					}
				}
				catch(Exception e) {
					rval.append("&amp;");
				}
			}
			else if(c=='"') {
				rval.append("&quot;");
			}
			else if(c=='>') {
				rval.append("&gt;");
			}
			else if(c=='<') {
				rval.append("&lt;");
			}
			else if(c=='\r') {
				// do nothing, we don't want these
			}
			else if(((int)c) > 127) {
				rval.append("&#"+((int)c)+";");
			}
			else {
				rval.append(c);
			}
		}
		return(rval.toString());
	}

	public String createTinyURL(String url)
	{
		HTTPLoader tinyLoader = new HTTPLoader();
		tinyLoader.charset = "UTF-8";
		tinyLoader.setAllowForeignCookies(true);

		String rval = tinyLoader.getURL("http://tinyurl.com/api-create.php?url="+HTTPClient.convert(url));
		if(rval.startsWith("http://tinyurl.com/")) {
			return(rval);
		}
		else {
			return(null);
		}
	}

	protected String getPaste(String id)
	{
		String rval = null;
		BufferedReader in = null;
		try {
			File parent = new File(pastesDir);
			String[] list = parent.list();
			String realName = null;
			for(int x=0;x<list.length;x++) {
				if(list[x].startsWith(id+"-")) {
					realName = list[x];
					break;
				}
			}
			if(realName!=null) {
				File pasteFile = new File(parent,realName);
				in = new BufferedReader(new FileReader(pasteFile));
				StringBuffer message = new StringBuffer();
				String line = in.readLine();
				while(line!=null) {
					message.append(line);
					message.append("\n");
					line = in.readLine();
				}
				rval = message.toString();
			}
		}
		catch(Exception e) {e.printStackTrace();}
		finally {
			try {
				in.close();
			}
			catch(Exception e) {;}
		}
		return(rval);
	}

	protected Vector listPastes()
	{
		Vector rval = new Vector();
		File parent = new File(pastesDir);
		String[] list = parent.list();
		for(int x=0;x<list.length;x++) {
			int dash = list[x].indexOf("-");
			if(Character.isDigit(list[x].charAt(0)) && dash!=-1) {
				rval.addElement(new Pair(list[x].substring(0,dash),list[x].substring(dash+1)));
			}
		}
		return(StringTools.pairSort(rval));
	}

	protected void savePaste(String id, String user, String message)
	{
		FileWriter out = null;
		try {
			File parent = new File(pastesDir);
			File pasteFile = new File(parent,id+"-"+user);
			out = new FileWriter(pasteFile);
			out.write(message);
			out.flush();
		}
		catch(Exception e) {e.printStackTrace();}
		try {
			out.close();
		}
		catch(Exception e) {;}
	}

	protected int getNextTriviaQuestionIndex()
	{
		int rval = Math.abs(r.nextInt(triviaQuestions.size()));
		while(recentQuestions.contains(""+rval)) {
			rval = Math.abs(r.nextInt(triviaQuestions.size()));
		}
		recentQuestions.addElement(""+rval);
		if(recentQuestions.size()>50) {
			recentQuestions.removeElementAt(0);
		}
		return(rval);
	}

	protected synchronized void loadTrivia()
	{
		triviaQuestions = new Vector();
		triviaAnswers = new Vector();
		recentQuestions = new Vector();
		lastAnswer = "no trivia question has been revealed";

		try {
			File f = new File(triviaFile);
			BufferedReader fin= new BufferedReader(new FileReader(f));
			String line = fin.readLine();
			while(line!=null) {
				if(line.trim().length()>0) {
					triviaQuestions.addElement(line);
					line = fin.readLine();
					triviaAnswers.addElement(line);
				}
				line = fin.readLine();
			}
			fin.close();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	protected String makeJapanesePhrase()
	{
		StringBuffer rval = new StringBuffer();
		
		int numWords = Math.abs(r.nextInt(5)) + 3;
		
		rval.append(makeJapaneseWord());
		rval.append(" ");
		for(int x=1;x<numWords;x++) {
			rval.append(makeJapaneseWord());
			rval.append(" ");
			int randpart = r.nextInt(15);
			if(randpart==0) {
				rval.append("no ");
			}
			else if(randpart==1) {
				rval.append("wa ");
			}
			else if(randpart==2) {
				rval.append("ga ");
			}
			else if(randpart==3) {
				rval.append("no ");
			}
			else if(randpart==4) {
				rval.append("ka ");
			}
			else if(randpart==5) {
				rval.append("so ");
			}
		}

		return(rval.toString());
	}

	protected String makeJapaneseWord()
	{
		StringBuffer rval = new StringBuffer();

		int initialRand = r.nextInt(50);
		if(initialRand==0) {
			rval.append("watashi");
		}
		else if(initialRand==1) {
			rval.append("ware");
		}
		else if(initialRand==2) {
			rval.append("tobiko");
		}
		else if(initialRand==3) {
			rval.append("pantsu");
		}
		else if(initialRand==4) {
			rval.append("sushi");
		}
		else if(initialRand==5) {
			rval.append("nande");
		}
		else if(initialRand==6) {
			rval.append("nani");
		}
		else if(initialRand==7) {
			rval.append("baka");
		}
		else if(initialRand==8) {
			rval.append("yuri");
		}
		else if(initialRand==9) {
			rval.append("boku");
		}
		else if(initialRand==10) {
			rval.append("ichi");
		}
		else if(initialRand==11) {
			rval.append("nihon");
		}
		else {
			int length = 1;

			if(r.nextInt(2)==0) {
				length = Math.abs(r.nextInt(4)) + 1;
			}
			if(r.nextInt(20)==0) {
				length = 5;
			}
			else if(r.nextInt(25)==0) {
				length = 6;
			}
			
			rval.append(japanese[Math.abs(r.nextInt(japanese.length))]);
			
			for(int x=1;x<length;x++) {
				rval.append(japanese[Math.abs(r.nextInt(japanese.length))]);
				if(r.nextInt(10)==0) {
					rval.append("n");
				}
			}
		}

		return(rval.toString());
	}

	protected String[] makeJapanese()
	{
		Vector rval = new Vector();
		rval.addElement("a");
		rval.addElement("e");
		rval.addElement("u");
		rval.addElement("i");
		rval.addElement("o");
		rval.addElement("ka");
		rval.addElement("ke");
		rval.addElement("ku");
		rval.addElement("ki");
		rval.addElement("ko");
		rval.addElement("sa");
		rval.addElement("se");
		rval.addElement("su");
		rval.addElement("shi");
		rval.addElement("so");
		rval.addElement("ta");
		rval.addElement("te");
		rval.addElement("tsu");
		rval.addElement("chi");
		rval.addElement("to");
		rval.addElement("na");
		rval.addElement("ne");
		rval.addElement("nu");
		rval.addElement("ni");
		rval.addElement("no");
		rval.addElement("ha");
		rval.addElement("he");
		rval.addElement("fu");
		rval.addElement("hi");
		rval.addElement("ho");
		rval.addElement("ma");
		rval.addElement("me");
		rval.addElement("mu");
		rval.addElement("mi");
		rval.addElement("mo");
		rval.addElement("ya");
		rval.addElement("yu");
		rval.addElement("yo");
		rval.addElement("ra");
		rval.addElement("re");
		rval.addElement("ru");
		rval.addElement("ri");
		rval.addElement("ro");
		rval.addElement("wa");
		rval.addElement("wo");

		String[] rvalArray = new String[rval.size()];
		for(int x=0;x<rvalArray.length;x++) {
			rvalArray[x] = (String)rval.elementAt(x);
		}

		return(rvalArray);
	}

	public static void main(String[] args)
	{
		new IRCBot2(args);
	}



	class BuyTable extends Thread
	{
		Hashtable lastBuy;
		String firstSolarURL = "http://finance.yahoo.com/q?s=FSLR&ql=1"; // $110
		String baiduURL = "http://finance.yahoo.com/q?s=BIDU&ql=1"; // $182
		String googleURL = "http://finance.yahoo.com/q?s=GOOG&ql=1"; // $291
		String bankAmericaURL = "http://finance.yahoo.com/q?s=BAC-PL&ql=1"; // $666
		String seaboardCorpURL = "http://finance.yahoo.com/q?s=SEB&ql=1"; // $992
		String berkshireHathawayURL = "http://finance.yahoo.com/q?s=BRK-A&ql=1"; // $103,333.00

		int triggerPrice = 1000;
		int voicePrice = 2000;
		int topicPrice = 3000;
		int kickPrice = 4000;
		int kickwordPrice = 5000;
		int cannonPrice = 4000;
		int halfopsPrice = 500000;

		HTTPLoader buyLoader = new HTTPLoader();

		public BuyTable()
		{
			lastBuy = new Hashtable();
			setName("Stock Price Thread");
			start();
		}

		public String getPriceString()
		{
			return("The current market prices are: trigger word=$"+triggerPrice+" voice=$"+voicePrice+" topic=$"+topicPrice+" kick=$"+kickPrice+" kickword=$"+kickwordPrice+" kick cannon reset=$"+cannonPrice+" half ops=$"+halfopsPrice);
		}

		public boolean buyTrigger(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>triggerPrice*100) {
							worth = worth - triggerPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyCannon(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>cannonPrice*100) {
							worth = worth - cannonPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyVoice(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>voicePrice*100) {
							worth = worth - voicePrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyTopic(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>topicPrice*100) {
							worth = worth - topicPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyKick(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>kickPrice*100) {
							worth = worth - kickPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyKickword(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>kickwordPrice*100) {
							worth = worth - kickwordPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public boolean buyHalfops(String user)
		{
			boolean rval = false;
			String last = (String)lastBuy.get(user);
			long stamp = 0;
			if(last!=null) {
				stamp = Long.valueOf(last).longValue();
			}
			if(System.currentTimeMillis()-stamp > 1800000) {
				synchronized(worthTable) {
					Pair p = (Pair)worthTable.get(user);
					if(p!=null) {
						int worth = Integer.parseInt(p.second().toString());
						if(worth>halfopsPrice*100) {
							worth = worth - halfopsPrice*100;
							Pair p2 = new Pair(p.first(),worth+"");
							worthTable.put(user,p2);
							lastBuy.put(user,System.currentTimeMillis()+"");
							rval = true;
						}
					}
				}
			}
			return(rval);
		}

		public void run()
		{
			while(true) {
				try {
					triggerPrice = getPrice(firstSolarURL) * 10;
					voicePrice = getPrice(baiduURL) * 10;
					topicPrice = getPrice(googleURL) * 10;
					kickPrice = getPrice(bankAmericaURL) * 7;
					kickwordPrice = getPrice(seaboardCorpURL) * 7;
					cannonPrice = getPrice(seaboardCorpURL) * 4;
					halfopsPrice = getPrice(berkshireHathawayURL) * 3;
				}
				catch(Exception e) {
					System.out.println("Fuck... "+e);
					e.printStackTrace();
				}
				try {
					sleep(60000 * 600);
				}
				catch(Exception e) {e.printStackTrace();}
			}
		}

		public int getPrice(String url)
		{
			String page = buyLoader.getURL(url);
			int index = page.indexOf("Last Trade:");
			index = page.indexOf("<big>",index);
			index = page.indexOf("<span id=\"yfs",index);
			index = page.indexOf(">",index);
			page = page.substring(index+1);
			index = page.indexOf("</span");
			page = page.substring(0,index);
			System.out.println("price="+page);
			index = page.indexOf(",");
			if(index!=-1) {
				page = page.substring(0,index)+page.substring(index+1);
			}
			index = page.indexOf(".");
			page = page.substring(0,index);
			return(Integer.parseInt(page));
		}

	}


	class GainaxToppageThread extends Thread
	{
		HTTPLoader gloader;
		String lastImage = "";

		public GainaxToppageThread()
		{
			gloader = new HTTPLoader();

			setName("GAINAX Top page thread");

			init();

			start();
		}

		public void init()
		{
			lastImage = getImage();
		}

		public void run()
		{
			while(true) {
				try {
					sleep(60000 * 60);

					String newImage = getImage();
					if(!newImage.equals(lastImage)) {
						lastImage = newImage;
						writeMsg(myChan, "New Gainax Top page Image: "+lastImage);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		public String getImage()
		{
			String frontpage = gloader.getURL("http://www.gainax.co.jp/");
			try {
				int index = frontpage.indexOf("<a href=\"top.html\">");
				if(index!=-1) {
					index = frontpage.indexOf("src=\"",index);
					frontpage = frontpage.substring(index+5);
					index = frontpage.indexOf("\"");
					frontpage = "http://www.gainax.co.jp/" + frontpage.substring(0,index);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(frontpage.startsWith("http:")) {
				return(frontpage);
			}
			else {
				return("");
			}
		}

	}

	class LoliTable
	{
		Hashtable lastLoliTimes;
		Hashtable table;
		String filename;
		
		public LoliTable(String f)
		{
			filename = f;
			table = new Hashtable();
			lastLoliTimes = new Hashtable();
			readFromFile();
		}

		public void combineUsers(String user1, String user2)
		{
			int loli1 = getLolis(user1);
			int loli2 = getLolis(user2);

			if(table.containsKey(user2.toLowerCase())) {
				Pair p = (Pair)table.get(user2.toLowerCase());
				p.setFirst((loli1+loli2)+"");
				table.put(user2.toLowerCase(),p);
			}
			else if(table.containsKey(user1.toLowerCase())) {
				Pair p = (Pair)table.get(user1.toLowerCase());
				table.put(user2.toLowerCase(),p);
			}
			table.remove(user1.toLowerCase());
		}

		public String[] getUsers()
		{
			String[] rval = new String[0];
			Vector v = new Vector();
			Enumeration e = table.keys();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				Pair p = (Pair)table.get(user);
				int icount = Integer.parseInt((String)p.first());
				String count = icount+"";
				if(icount<10) {
					count = "000000000"+count;
				}
				else if(icount<100) {
					count = "00000000"+count;
				}
				else if(icount<1000) {
					count = "0000000"+count;
				}
				else if(icount<10000) {
					count = "000000"+count;
				}
				else if(icount<100000) {
					count = "00000"+count;
				}
				else if(icount<1000000) {
					count = "0000"+count;
				}
				else if(icount<10000000) {
					count = "000"+count;
				}
				else if(icount<100000000) {
					count = "00"+count;
				}
				else if(icount<1000000000) {
					count = "0"+count;
				}
				v.addElement(new Pair(count,user));
			}
			Vector sorted = StringTools.pairSort(v);
			rval = new String[sorted.size()];
			int count = 0;
			for(int x=rval.length-1;x>=0;x--) {
				rval[count++] = (String)((Pair)sorted.elementAt(x)).second();
			}
			return(rval);
		}

		public int getLolis(String user)
		{
			if(table.containsKey(user)) {
				return(Integer.parseInt((String)((Pair)table.get(user.toLowerCase())).first()));
			}
			else {
				return(0);
			}
		}

		public void askLolis(int rank)
		{
			String[] ranks = getUsers();
			if(ranks.length>=rank) {
				Pair p = (Pair)table.get(ranks[rank-1]);
				writeMsg(myChan,BOLD+ranks[rank-1]+BOLD+" is ranked at #"+rank+" with a total of "+Integer.parseInt(p.first().toString())+" Lolis");
			}
			else {
				writeMsg(myChan,"There's nobody in the channel ranked #"+rank);
			}
		}

		public void askLolis(String user, String realUser)
		{
			System.out.println("user="+user+" realUser="+realUser);
			if(realUser.equalsIgnoreCase(myNick)) {
				writeMsg(myChan,BOLD+realUser+BOLD+" has a total of "+r.nextInt()+" Lolis, and ranked #"+r.nextInt()+" out of "+r.nextInt()+" people with Lolis");
			}
			else {
				Pair p = (Pair)table.get(user.toLowerCase());
				if(p==null) {
					writeMsg(myChan,BOLD+realUser+BOLD+"? Who's that?");
				}
				else {
					String[] ranks = getUsers();
					int x = 0;
					for(x=0;x<ranks.length;x++) {
						if(ranks[x].equalsIgnoreCase(user)) {
							break;
						}
					}
					int loliCount = Integer.parseInt((String)p.first());
					if(loliCount==0) {
						writeMsg(myChan,BOLD+realUser+BOLD+" has no Lolis");
					}
					else if(loliCount==1) {
						writeMsg(myChan,BOLD+realUser+BOLD+" has a total of 1 Loli, and ranked #"+(x+1)+" out of "+ranks.length+" people with Lolis");
					}
					else {
						writeMsg(myChan,BOLD+realUser+BOLD+" has a total of "+loliCount+" Lolis, and ranked #"+(x+1)+" out of "+ranks.length+" people with Lolis");
					}
				}
			}
		}

		public void tryLolis(String user, String realUser)
		{
			Pair p = (Pair)table.get(user.toLowerCase());
			if(p==null) {
				p = new Pair("0","0");
				table.put(user.toLowerCase(),p);
			}
			long lastLoli = Long.valueOf((String)p.second()).longValue();
			int loliCount = Integer.parseInt((String)p.first());
			long loliTimeDiff = System.currentTimeMillis()-lastLoli;
			if(loliTimeDiff <= (loliTimeout+loliDiff)) {
				System.out.println(System.currentTimeMillis()+" - lastLoli="+lastLoli+" loliTimeDiff="+loliTimeDiff+" loliTimeout="+loliTimeout);
				String time = "";
				int timeleft = (int)(loliTimeDiff);
				timeleft = (int)(timeleft/1000);
				int seconds = timeleft % 60;
				timeleft = (int)(timeleft/60);
				int minutes = timeleft % 60;
				timeleft = (int)(timeleft/60);
				if(timeleft>0) {
					if(timeleft==1) {
						time = time + timeleft + " hour, ";
					}
					else {
						time = time + timeleft + " hours, ";
					}
				}
				if(minutes > 0) {
					if(minutes==1) {
						time = time + minutes + " minute, ";
					}
					else {
						time = time + minutes + " minutes, ";
					}
				}
				if(seconds > 0) {
					if(seconds==1) {
						time = time + seconds + " second, ";
					}
					else {
						time = time + seconds + " seconds, ";
					}
				}
				//				writeMsg(realUser,"You have already asked for Lolis, you must wait "+time+"before you can ask again");
				writeMsg(realUser,"You asked for Lolis "+time+"ago. You need to wait longer before you can ask again");
			}
			else {
				boolean award = false;
				boolean badInterval = false;
				if(lastLoliTimes.containsKey(user.toLowerCase())) {
					long lastInterval = Long.valueOf(lastLoliTimes.get(user.toLowerCase()).toString()).longValue();
					System.out.println("LOLI: Last interval for "+user+": "+lastInterval+"  this interval: "+loliTimeDiff);
					if(Math.abs(lastInterval - loliTimeDiff) < 5000) {
						badInterval = true;
					}
				}
				lastLoliTimes.put(user.toLowerCase(),loliTimeDiff+"");

				char c = (char)1;
				
				String loliName = "Asuka Langley Soryu";
				int tmp = r.nextInt(7);
				if(tmp<-2) {
					loliName = "Rei Ayanami";
				}
				else if(tmp==0) {
					loliName = "Mana Kirishima";
				}
				else if(tmp==1 || tmp==-1) {
					loliName = "Hikari Horaki";
				}
				else if(tmp==2) {
					loliName = "Mayumi Yamagishi";
				}
				else if(tmp==-2) {
					loliName = "Mari Makinami";
				}

				if(badInterval) {
					writeMsg(myChan,"I don't feel like giving "+BOLD+realUser+BOLD+" any Loli.");
				}
				else {
					boolean killLoli = false;
					if(loliCount>100) {
						int base = (int)(loliCount * loliKillMult) + loliKillBase;
						int loliRoll = Math.abs(r.nextInt(loliKillRoll));

						System.out.println("Base = "+base+" roll = "+loliRoll);
						if(loliRoll < base) {
							killLoli = true;
						}

					}

					if(killLoli) {
						tmp = Math.abs(r.nextInt(15));
						System.out.println("Loli Kill roll = "+tmp);
						// 8 in 15
						if(tmp<8) {
							loliCount = loliCount - 1;
							writeMsg(myChan,c+"ACTION shoots one of "+realUser+"'s Mari Makinami's, "+realUser+" now has a total of "+loliCount+c);
						}
						// 4 in 15
						else if(tmp==8 || tmp==9 || tmp==10 || tmp==11) {
							loliCount = loliCount - 2;
							writeMsg(myChan,c+"ACTION poisons two of "+realUser+"'s Asuka's, "+realUser+" now has a total of "+loliCount+c);
						}
						// 2 in 15
						else if(tmp==12 || tmp==13) {
							loliCount = loliCount - 3;
							writeMsg(myChan,c+"ACTION drowns three of "+realUser+"'s Hikari's, "+realUser+" now has a total of "+loliCount+c);
						}
						// 1 in 15
						else {
							loliCount = loliCount - 4;
							writeMsg(myChan,c+"ACTION strangles four of "+realUser+"'s Rei's, "+realUser+" now has a total of "+loliCount+c);
						}
					}
					else {
						tmp = Math.abs(r.nextInt(30));
						// 2 in 30
						if(tmp==0 || tmp==1) {
							writeMsg(myChan,c+"ACTION kicks "+BOLD+realUser+BOLD+" in the shin."+c);
						}
						// 1 in 30
						else if(tmp==2) { // || tmp==3) {
							writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" a loli but it was some sifeet. (http://i185.photobucket.com/albums/x6/glittertit/pussyfoot.jpg)"+c);
						}
						// 1 in 30
						else if(tmp==3) {
							writeMsg(myChan,realUser+": No LOLI for you, faggot.");
						}
						// 2 in 30
						else if(tmp==4 || tmp==5) {
							writeMsg(myChan,c+"ACTION hands "+BOLD+realUser+BOLD+" a bag of farts."+c);
						}
						// 1 in 30
						else if(tmp==6) {
							loliCount = loliCount+6;
							writeMsg(myChan,c+"ACTION gives the SUPER PEDO "+BOLD+realUser+BOLD+" 6 "+loliName+"'s!, "+realUser+" now has a total of "+loliCount+" Lolis"+c);
							award = true;
						}
						// 2 in 30
						else if(tmp==7 || tmp==8) {
							loliCount = loliCount+5;
							writeMsg(myChan,c+"ACTION gives the PEDO "+BOLD+realUser+BOLD+" 5 "+loliName+"'s!, "+realUser+" now has a total of "+loliCount+" Lolis"+c);
							award = true;
						}
						else {
							award = true;
							int lolis = 1;
							boolean special = false;
							String tmpString = " Loli";
							// 3 in 30
							if(tmp==9 || tmp==10 || tmp==11) {
								lolis = 4;
							}
							// 4 in 30
							else if(tmp==12 || tmp==13 || tmp==14 || tmp==15) {
								lolis = 3;
							}
							// 6 in 30
							else if(tmp<22) {
								lolis = 2;
							}
							// 8 in 30  (1 loli)
							else {
								// 1 in 20
								if(Math.abs(r.nextInt(20))==0) {
									special = true;
								}
							}

							loliCount = loliCount + lolis;
							if(loliCount>1) {
								tmpString = " Lolis";
							}
							
							if(special) {
								tmp = r.nextInt(6);
								// 3 in 6
								if(tmp==0 || tmp==1 || tmp==2) {
									writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" a Shinji Ikari (wut?), "+realUser+" now has a total of "+loliCount+tmpString+c);
								}
								// 2 in 6
								else if(tmp==3 || tmp==4) {
									writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" a Kaworu Nagisa (oh shit), "+realUser+" now has a total of "+loliCount+tmpString+c);
								}
								// 1 in 6
								else {
									writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" a PenPen (...), "+realUser+" now has a total of "+loliCount+tmpString+c);
								}
							}
							else if(lolis==1) {
								if(loliName.startsWith("Asuka")) {
									loliName = "n "+loliName;
								}
								else {
									loliName = " "+loliName;
								}
								writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" a"+loliName+", "+realUser+" now has a total of "+loliCount+tmpString+c);
							}
							else {
								writeMsg(myChan,c+"ACTION gives "+BOLD+realUser+BOLD+" "+lolis+" "+loliName+"'s, "+realUser+" now has a total of "+loliCount+" Lolis"+c);
							}
						}
					}
				}
				p.setFirst(""+loliCount);
				p.setSecond(""+System.currentTimeMillis());

				if(award) {
					award(user, realUser, loliCount);
				}
				loliDiff = Math.abs(r.nextInt(1200000));
				System.out.println("Loli Diff = "+loliDiff);
			}
		}


		public void award(String user, String realUser, int loliCount)
		{
			int tmpLoliCount = loliCount;
			if(tmpLoliCount > 450) {
				tmpLoliCount = tmpLoliCount%450;
			}
			if(shitList.contains(user.toLowerCase())) {
				return;
			}
			if(tmpLoliCount==50 || tmpLoliCount==51 ||tmpLoliCount==52 ||tmpLoliCount==53 ||tmpLoliCount==54 ||
				tmpLoliCount==90 || tmpLoliCount==91 ||tmpLoliCount==92 ||tmpLoliCount==93 ||tmpLoliCount==94 ||
				tmpLoliCount==110 || tmpLoliCount==111 ||tmpLoliCount==112 ||tmpLoliCount==113 ||tmpLoliCount==114 ||
				tmpLoliCount==180 || tmpLoliCount==181 ||tmpLoliCount==182 ||tmpLoliCount==183 ||tmpLoliCount==184 

				) {
				boolean award = false;
				if(voiceAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)voiceAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins voice: "+loliCount);
					}
					else {
						writeMsg(myChan,"With a total of "+loliCount+" Lolis, "+realUser+" wins a voice.");
						write("MODE "+myChan+" +v "+realUser);
					}
					voiceAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if (tmpLoliCount==160 || tmpLoliCount==161 ||tmpLoliCount==162 ||tmpLoliCount==163 ||tmpLoliCount==164 ||
						tmpLoliCount==230 || tmpLoliCount==231 ||tmpLoliCount==232 ||tmpLoliCount==233 ||tmpLoliCount==234 ||
						tmpLoliCount==380 || tmpLoliCount==381 ||tmpLoliCount==382 ||tmpLoliCount==383 ||tmpLoliCount==384

						) {
				boolean award = false;
				if(randomAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)randomAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					writeMsg(realUser,"You have "+loliCount+" Lolis, you win a chance to add an internal message.");
					writeMsg(realUser,"/msg me with the message, you only get one chance, hurry before I forget.");
					randomAward = realUser;
					randomAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==80 || tmpLoliCount==81 || tmpLoliCount==82 || tmpLoliCount==83 || tmpLoliCount==84 ||
					  tmpLoliCount==240 || tmpLoliCount==241 ||tmpLoliCount==242 ||tmpLoliCount==243 ||tmpLoliCount==244 ||
					  tmpLoliCount==330 || tmpLoliCount==331 ||tmpLoliCount==332 ||tmpLoliCount==333 ||tmpLoliCount==334 

					  ) {
				boolean award = false;
				if(triggerAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)triggerAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins trigger: "+loliCount);
					}
					else {
						writeMsg(realUser,"You have "+loliCount+" Lolis, you win the chance to set the trigger word.");
						writeMsg(realUser,"/msg me with the word, you only get one chance, hurry before I forget.");
						triggerAward = realUser;
					}
					triggerAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==140 || tmpLoliCount==141 ||tmpLoliCount==142 ||tmpLoliCount==143 ||tmpLoliCount==144 ||
					  tmpLoliCount==220 || tmpLoliCount==221 ||tmpLoliCount==222 ||tmpLoliCount==223 ||tmpLoliCount==224

					  ) {
				boolean award = false;
				if(kickAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)kickAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins kick: "+loliCount);
					}
					else {
						writeMsg(realUser,"You have "+loliCount+" Lolis, you win the chance to kick someone from the channel.");
						writeMsg(realUser,"/msg me with the nick of the person you want to kick, you only get one chance, hurry before I forget.");
						kickAward = realUser;
					}
					kickAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==150 || tmpLoliCount==151 ||tmpLoliCount==152 ||tmpLoliCount==153 ||tmpLoliCount==154 ||
					  tmpLoliCount==260 || tmpLoliCount==261 ||tmpLoliCount==262 ||tmpLoliCount==263 ||tmpLoliCount==264
					  ) {
				boolean award = false;
				if(kickWordAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)kickWordAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins kick word: "+loliCount);
					}
					else {
						writeMsg(realUser,"You have "+loliCount+" Lolis, you win the chance to set a kick word. Anyone who says something containing this word will be kicked.");
						writeMsg(realUser,"/msg me with the word you want to make a kick word, you only get one chance, hurry before I forget.");
						kickWordAward = realUser;
					}
					kickWordAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==190 || tmpLoliCount==191 ||tmpLoliCount==192 ||tmpLoliCount==193 ||tmpLoliCount==194 ||
					  tmpLoliCount==300 || tmpLoliCount==301 ||tmpLoliCount==302 ||tmpLoliCount==303 ||tmpLoliCount==304

					  ) {
				boolean award = false;
				if(topicAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)topicAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins topic: "+loliCount);
					}
					else {
						writeMsg(realUser,"You have "+loliCount+" Lolis, you win the chance to set the channel's topic.");
						writeMsg(realUser,"/msg me with what you want the topic to be, you only get one chance, hurry before I forget.");
						topicAward = realUser;
					}
					topicAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==290 || tmpLoliCount==291 ||tmpLoliCount==292 ||tmpLoliCount==293 ||tmpLoliCount==294 ||
					  tmpLoliCount==390 || tmpLoliCount==391 ||tmpLoliCount==392 ||tmpLoliCount==393 ||tmpLoliCount==394 ||
					  tmpLoliCount==130 || tmpLoliCount==131 ||tmpLoliCount==132 ||tmpLoliCount==133 ||tmpLoliCount==134

					  ) {
				boolean award = false;
				if(loliMessageAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)loliMessageAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins message: "+loliCount);
					}
					else {
						writeMsg(realUser,"You have "+loliCount+" Lolis, you win the chance to set a message on the channel stats page.");
						writeMsg(realUser,"/msg me with what you want the message to be, you only get one chance, hurry before I forget.");
						loliMessageAward = realUser;
					}
					loliMessageAwards.put(user.toLowerCase(),""+loliCount);
				}
			}
			else if(tmpLoliCount==430 || tmpLoliCount==431 ||tmpLoliCount==432 ||tmpLoliCount==433 ||tmpLoliCount==434 ||tmpLoliCount==435) {
				boolean award = false;
				if(halfopsAwards.containsKey(user.toLowerCase())) {
					int lastLolis = Integer.parseInt((String)halfopsAwards.get(user.toLowerCase()));
					if(loliCount>lastLolis+4) {
						award = true;
					}
				}
				else {
					award = true;
				}

				if(award) {
					if(user.equalsIgnoreCase("ornette")) {
						writeMsg("Ornette","Ornette wins halfops: "+loliCount);
					}
					else if(user.equalsIgnoreCase("reichu") || user.equalsIgnoreCase("brikhaus") || user.equalsIgnoreCase("zapalacx")) {
						writeMsg(myChan,"With a total of "+loliCount+" Lolis, "+realUser+" wins a halfops, but what the fuck would the point of that be?");
						delay(2000,1000);
						writeMsg(myChan,"huh? Right, no point at all...");
					}
					else {
						writeMsg(myChan,"With a total of "+loliCount+" Lolis, "+realUser+" wins halfops!!! HOLY SHIT!!!");
						write("MODE "+myChan+" +h "+realUser);
						delay(loliCount*10,0);
						delay(10000,6000);
						write("MODE "+myChan+" -h "+realUser);
						writeMsg(myChan,"Sucker...");
						delay(30000,10000);
						writeMsg(myChan,"P.S. you suck cocks.");
					}
				}
				halfopsAwards.put(user.toLowerCase(),""+loliCount);
			}
		} 



		public void readFromFile()
		{
			File f = new File(filename);
			BufferedReader in = null;
			String line = "";
			try {
				in = new BufferedReader(new FileReader(f));

				line = in.readLine();
				if(line.trim().length()>0) {
					loliMessage = line;
				}
				line = in.readLine();
				while(line!=null) {
					if(line.trim().length()>0) {
						int index = line.indexOf(" ");
						String username = line.substring(0,index);
						line = line.substring(index).trim();
						index = line.indexOf(" ");
						Pair p = new Pair(line.substring(0,index),line.substring(index).trim());
						table.put(username.toLowerCase(),p);
					}
					line = in.readLine();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					in.close();
				}
				catch(Exception e) {;}
			}
		}

		public void writeToFile()
		{
			File f = new File(filename);
			FileWriter out = null;

			try {
				out = new FileWriter(f);
				if(loliMessage==null) {
					out.write("\n");
				}
				else {
					out.write(loliMessage+"\n");
				}
				Enumeration e = table.keys();

				while(e.hasMoreElements()) {
					String username = (String)e.nextElement();
					Pair p = (Pair)table.get(username.toLowerCase());
					out.write(username+" "+p.first()+" "+p.second()+"\n");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					out.close();
				}
				catch(Exception e) {;}
			}
		}
	}



	class WorkKMethod extends KMethod
	{
		String method;
		String[] params;
		String names = "";
		
		public WorkKMethod(String methodType, String[] methodParams)
		{
			method = methodType;
			params = methodParams;
			for(int x=0;x<methodParams.length;x++) {
				names = names+" \""+methodParams[x]+"\"";
			}
		}

		public void execute()
		{
			Thread.currentThread().setName(method+names);
			try {
				if(method.equals("addRecent")) {
					boolean bool = true;
					if(params[3].equals("false")) {
						bool = false;
					}
					addRecent(params[0],params[1],params[2],bool);
				}
				else if(method.equals("parseStats")) {
					parseStats(params[0],params[1]);
				}
				else if(method.equals("parseMsg")) {
					parseMsg(params[0],params[1],params[2]);
				}
				else if(method.equals("parseQuit")) {
					parseQuit(params[0],params[1],params[2]);
				}
				else if(method.equals("parseJoin")) {
					parseJoin(params[0],params[1],params[2]);
				}
				else if(method.equals("parseNick")) {
					parseNick(params[0],params[1],params[2]);
				}
				else if(method.equals("parseScript")) {
					parseScript(params[0],params[1],params[2]);
				}
				else if(method.equals("parseFortune")) {
					parseFortune(params[0],params[1],params[2],params[3]);
				}
				else if(method.equals("parseYoutube")) {
					parseYoutube(params[0],params[1],params[2]);
				}
				else if(method.equals("parseAnime")) {
					parseAnime(params[0],params[1],params[2]);
				}
				else if(method.equals("parseIMDB")) {
					parseIMDB(params[0],params[1],params[2]);
				}
				else if(method.equals("parsePantsu")) {
					parsePantsu(params[0],params[1],params[2]);
				}
				else if(method.equals("parseBoobies")) {
					parseBoobies(params[0],params[1],params[2]);
				}
				else if(method.equals("parseKick")) {
					parseKick(params[0],params[1],params[2]);
				}
				else if(method.equals("parseMOAR")) {
					parseMOAR(params[0],params[1]);
				}
				else if(method.equals("parseLESS")) {
					parseLESS(params[0],params[1]);
				}
				else if(method.equals("parseLoli")) {
					parseLoli(params[0],params[1],params[2]);
				}
				else if(method.equals("parseWorth")) {
					parseWorth(params[0],params[1],params[2]);
				}
				else if(method.equals("parseWakamoto")) {
					parseWakamoto(params[0],params[1],params[2]);
				}
				else if(method.equals("parseRoll")) {
					parseRoll(params[0],params[1],params[2]);
				}
				else if(method.equals("parse8Ball")) {
					parse8Ball(params[0],params[1],params[2]);
				}
				else if(method.equals("parseTrigger")) {
					parseTrigger(params[0],params[1],params[2]);
				}
				else if(method.equals("parseTrivia")) {
					parseTrivia(params[0],params[1],params[2]);
				}
				else if(method.equals("parseEliza")) {
					parseEliza(params[0],params[1],params[2]);
				}
				else if(method.equals("parseGame")) {
					parseGame(params[0],params[1],params[2]);
				}
				else if(method.equals("pong")) {
					pong(params[0]);
				}
				else if(method.equals("googleTrends")) {
					googleTrends();
				}
				else if(method.equals("printStats")) {
					printStats(params[0],params[1]);
				}
				else if(method.equals("admin")) {
					admin(params[0],params[1]);
				}
				else if(method.equals("updateUserList")) {
					updateUserList();
				}
				else if(method.equals("updateStats")) {
					updateStats(params[0],params[1],params[2]);
				}
				else if(method.equals("processRSS")) {
					processRSS(params[0],params[1]);
				}
				else {
					System.out.println("KMETHOD: Don't understand method="+method);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			Thread.currentThread().setName("IDLE");
		}
	}


	class STDINReader extends Thread
	{
		public STDINReader()
		{
			start();
		}

		public void run()
		{
			//			BufferedReader stdin = null;
			try {
				//				stdin = new BufferedReader(new InputStreamReader(System.in));
				String line = readLine(System.in);
				while(line!=null) {
					writeMsg(myChan,line);
					line = readLine(System.in);
					if(line.startsWith("SERVER")) {
						ircServer = line.substring(6).trim();
						try {
							ircSock.close();
						}
						catch(Exception e) {;}
					}
					else if(line.startsWith("RSSRESTART")) {
						try {
							rss.runThread.stop(new InterruptedException());
						}
						catch(Throwable t) {
							t.printStackTrace();
						}
						rss = new IRCBot2Rss(IRCBot2.this);
					}
					else if(line.startsWith("THREADS")) {
						printThreads();
						line = readLine(System.in);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	
	}


	class HTTPThread extends Thread
	{
		String httpHost = "benito.cometway.com";
		int httpPort = 8000;
			
		public HTTPThread()
		{
			setName("HTTP Thread");
			start();
		}

		public void run()
		{
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(httpPort);
				while(true) {
					try {
						Socket sock = ss.accept();
						sock.setSoTimeout(15000);
						new HTTPHandler(sock,httpHost,httpPort);
					}
					catch(Exception e) {
						e.printStackTrace();
						ss.close();
						ss = new ServerSocket(httpPort);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				writeMsg(myChan,"OK, make sure Ornette knows I'm broken, can't bind to port "+httpPort);
			}
		}
	}

	class HTTPHandler extends Thread
	{
		String httpHost;
		int httpPort;
		InputStream httpIn;
		OutputStream httpOut;
		Socket sock;

		public HTTPHandler(Socket sck, String host, int port)
		{
			httpHost = host;
			httpPort = port;
			try {
				httpIn = sck.getInputStream();
				httpOut = sck.getOutputStream();
				sock = sck;
				start();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

	    public String readLineHTTP() throws IOException
	    {
		int count = 0;
		StringBuffer rval = new StringBuffer();
		char c = (char)httpIn.read();
		while(c != '\r' && c!= '\n' && ((int)c)!=-1 && count<200) {
		    count++;
		    rval.append(c);
		    c = (char)httpIn.read();
		}
		if(c=='\r') {
		    httpIn.read();
		}
		return(rval.toString());
	    }

		public void run()
		{
			try {
				String params = "";
				String line = readLineHTTP();
				System.out.println(sock.getInetAddress().getHostAddress()+" HTTP: "+line);
				try {
					String tmp = readLineHTTP();
					String ref = "";
					while(tmp.trim().length()>0) {
						if(tmp.toLowerCase().startsWith("referer:")) {
							ref = tmp.substring(8).trim();
							break;
						}
						tmp = readLineHTTP();
					}
					httpLog.write(sock.getInetAddress().getHostAddress()+" ["+(new Date())+"] \""+line+"\" - \""+ref+"\"\n");
					httpLog.flush();
				}
				catch(Exception e) {;}
				if(line.startsWith("GET")) {
					line = line.substring(3).trim();
				}
				else if(line.startsWith("POST")) {
					line = line.substring(4).trim();
					String tmpLine = readLineHTTP();
					int length = 0;
					while(tmpLine!=null && tmpLine.trim().length()>0) {
						if(tmpLine.toLowerCase().startsWith("content-length:")) {
							tmpLine = tmpLine.substring(15).trim();
							try {
								length = Integer.parseInt(tmpLine);
							}
							catch(Exception e) {e.printStackTrace();}
						}
						else if(tmpLine.trim().length()==0) {
							break;
						}
						tmpLine = readLineHTTP();
					}
					byte[] buffer = new byte[1024];
					int bytesRead = httpIn.read(buffer);
					int totalBytes = bytesRead;
					while(bytesRead>0) {
						params = params + new String(buffer,0,bytesRead,"UTF-8");
						if(totalBytes >= length) {
							break;
						}
						bytesRead = httpIn.read(buffer);
						totalBytes = totalBytes+bytesRead;
					}
				}
				else {
					line = null;
				}

				if(line!=null) {
					if(line.indexOf("?")!=-1) {
						params = params+line.substring(line.indexOf("?")+1);
						line = line.substring(0,line.indexOf("?"));
					}
					if(line.startsWith("/channel")) {
						writeChannel(params);
					}
					else if(line.startsWith("/user")) {
						writeUser(params);
					}
					else if(line.startsWith("/fortune")) {
						writeFortune(params);
					}
					else if(line.startsWith("/paste")) {
						writePaste(params);
					}
					else if(line.startsWith("/recent")) {
						writeRecent(sock.getInetAddress().getHostAddress());
					}
					else if(line.startsWith("/favicon.ico")) {
						writeIcon();
					}
					else if(line.startsWith("/rss.xml")) {
						//						writeRSS();
					}
					else {
						writeMainPage();
					}						

				}
				else {
					writeMainPage();
				}
			
				sleep(2000);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			try {httpOut.close();}catch(Exception e) {;}
			try {httpIn.close();}catch(Exception e) {;}
			try {sock.close();}catch(Exception e) {;}
		}

		public Hashtable parseParams(String params)
		{
			Hashtable rval = new Hashtable();
			if(params.trim().length()>0) {
				if(params.indexOf(" ")!=-1) {
					params = params.substring(0,params.indexOf(" ")).trim();
				}
				int index = params.indexOf("&");
				while(index!=-1) {
					String pair = params.substring(0,index);
					params = params.substring(index+1);
					index = pair.indexOf("=");
					if(index!=-1) {
						rval.put(com.cometway.httpd.HTMLStringTools.decode(pair.substring(0,index)),com.cometway.httpd.HTMLStringTools.decode(pair.substring(index+1)));
					}
					else {
						rval.put(com.cometway.httpd.HTMLStringTools.decode(pair),"");
					}
					index = params.indexOf("&");
				}
				if(params.length()>0) {
					index = params.indexOf("=");
					if(index!=-1) {
						rval.put(com.cometway.httpd.HTMLStringTools.decode(params.substring(0,index)),com.cometway.httpd.HTMLStringTools.decode(params.substring(index+1)));
					}
					else {
						rval.put(com.cometway.httpd.HTMLStringTools.decode(params),"");
					}
				}
			}
			return(rval);
		}
		/*
		  public void writeRSS()
		  {
		  StringBuffer out = new StringBuffer();
		  StringBuffer content = new StringBuffer();
		  out.append("HTTP/1.1 200 Ok\r\n");
		  out.append("Connection: close\r\n");
		  out.append("Content-Type: application/rss+xml; charset=utf-8; charset=utf-8\r\n");

		  String fortuneCommand = fortuneProgram;
		  if(fortuneCommand.indexOf("-s")!=-1) {
		  fortuneCommand = fortuneCommand.substring(0,fortuneCommand.indexOf("-s"))+fortuneCommand.substring(fortuneCommand.indexOf("-s")+2);
		  }
		  ExecuteCommand command = new ExecuteCommand(fortuneCommand);
		  StringBuffer output = new StringBuffer();
		  command.processOut = output;
		  command.finishedWaitTime = (200 + r.nextInt()%100);
		  command.execute();
		  String fortune = output.toString();

		  content.append("<?xml version=\"1.0\" ?>\n");
		  content.append("<rss version=\"2.0\">\n");
		  content.append("<channel>\n");
		  content.append("<title>Holy Shit! RSS!</title>\n");
		  content.append("<link>http://"+httpHost+":"+httpPort+"/rss.xml</link>\n");
		  content.append("<description>"+fortune+"</description>\n");
		  content.append("<copyright>No Rights, please don't sue me</copyright>\n");
		  Date lastUpdate = null;
		  for(int x=0;x<rssThreads.size();x++) {
		  RSSThread rss = (RSSThread)rssThreads.elementAt(x);
		  if(lastUpdate==null) {
		  lastUpdate = rss.lastUpdate;
		  }
		  else {
		  if(lastUpdate.before(rss.lastUpdate)) {
		  lastUpdate = rss.lastUpdate;
		  }
		  }
		  }
		  if(lastUpdate == null) {
		  System.out.println("Last update = null?");
		  lastUpdate = new Date();
		  }
		  content.append("<lastBuildDate>"+rssFormat.format(lastUpdate)+"</lastBuildDate>\n\n");

		  for(int x=0;x<rssThreads.size();x++) {
		  RSSThread rss = (RSSThread)rssThreads.elementAt(x);

		  if(rss.checkMatches) {
		  for(int z=0;z<rss.latestStories.size();z++) {
		  //					content.append("<item>\n");
		  content.append(rss.latestStories.elementAt(z).toString());
		  //					content.append("</item>\n\n");
		  content.append("\n\n");
		  }
		  }
		  }

		  content.append("</channel>\n");
		  content.append("</rss>\n");

		  out.append("Content-Length: "+content.length()+"\r\n\r\n");
			
		  try {
		  httpOut.write(out.toString().getBytes());
		  httpOut.write(content.toString().getBytes("UTF-8"));
		  httpOut.flush();
		  }
		  catch(Exception ex) {;}
		  }
		*/

		public void writeIcon()
		{
			try {
				httpOut.write(("HTTP/1.1 200 Ok\r\n").getBytes());
				httpOut.write(("Connection: close\r\n").getBytes());
				httpOut.write(("Content-Length: "+favicon.length+"\r\n").getBytes());
				httpOut.write(("Content-Type: image/x-icon\r\n\r\n").getBytes());
				httpOut.write(favicon);
				httpOut.flush();
			}
			catch(Exception e) {e.printStackTrace();}
		}

		public void writePaste(String params)
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");
			Hashtable cgi = parseParams(params);

			if(cgi.containsKey("message")) {
				content.append("<HTML><HEAD><TITLE>");
				content.append(myNick);
				content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Paste:</H1><P> ");
				String user = ((String)cgi.get("user")).toLowerCase();
				if(user==null) {
					user = "";
				}
				String message = (String)cgi.get("message");
				if(user.trim().length()==0) {
					content.append("<B>Invalid user</B><P>");
					content.append("<FORM METHOD=POST ACTION=/paste>Your Username: <INPUT TYPE=TEXT NAME=user VALUE='"+user+"' SIZE=15> Auto-Break long lines: <INPUT TYPE=CHECKBOX NAME=format VALUE=yes CHECKED><BR><TEXTAREA NAME=message COLS=80 ROWS=18 WRAP=SOFT></TEXTAREA><BR><INPUT TYPE=SUBMIT NAME=Paste VALUE=Paste><P>");
					content.append(writeFooter());
				}
				else if(!userList.containsKey(user)) {
					content.append("<B>You must be in the channel before you can paste</B><P>");
					content.append("<FORM METHOD=POST ACTION=/paste>Your Username: <INPUT TYPE=TEXT NAME=user VALUE='"+user+"' SIZE=15> Auto-Break long lines: <INPUT TYPE=CHECKBOX NAME=format VALUE=yes CHECKED><BR><TEXTAREA NAME=message COLS=80 ROWS=18 WRAP=SOFT></TEXTAREA><BR><INPUT TYPE=SUBMIT NAME=Paste VALUE=Paste><P>");
					content.append(writeFooter());
				}
				else if(message.trim().length()==0) {
					content.append("<B>Your message must not be empty</B><P>");
					content.append("<FORM METHOD=POST ACTION=/paste>Your Username: <INPUT TYPE=TEXT NAME=user VALUE='"+user+"' SIZE=15> Auto-Break long lines: <INPUT TYPE=CHECKBOX NAME=format VALUE=yes CHECKED><BR><TEXTAREA NAME=message COLS=80 ROWS=18 WRAP=SOFT></TEXTAREA><BR><INPUT TYPE=SUBMIT NAME=Paste VALUE=Paste><P>");
					content.append(writeFooter());
				}
				else if(message.trim().length()>150000) {
					content.append("<B>Your message is too long</B><P>");
					content.append("<FORM METHOD=POST ACTION=/paste>Your Username: <INPUT TYPE=TEXT NAME=user VALUE='"+user+"' SIZE=15> Auto-Break long lines: <INPUT TYPE=CHECKBOX NAME=format VALUE=yes CHECKED><BR><TEXTAREA NAME=message COLS=80 ROWS=18 WRAP=SOFT></TEXTAREA><BR><INPUT TYPE=SUBMIT NAME=Paste VALUE=Paste><P>");
					content.append(writeFooter());
				}					
				else {
					String id = System.currentTimeMillis()+Math.abs(r.nextInt(100000))+"";
					int index = message.indexOf("\n");
					try {
						if(cgi.containsKey("format") && ((String)cgi.get("format")).equals("yes")) {
							Vector lines = new Vector();
							int lastindex = 0;
							while(index!=-1) {
								lines.addElement(message.substring(lastindex,index));
								lastindex = index+1;
								index = message.indexOf("\n",lastindex);
							}
							if(lastindex<message.length()) {
								lines.addElement(message.substring(lastindex));
							}
							
							StringBuffer tmpBuffer = new StringBuffer();
							for(int x=0;x<lines.size();x++) {
								String tmp = (String)lines.elementAt(x);
								if(tmp.trim().length()==0) {
									tmpBuffer.append("\n");
								}
								else {
									while(tmp.length()>100) {
										String tmp2 = tmp.substring(0,100);
										int index2 = tmp2.lastIndexOf(" ");
										if(index2==-1) {
											index2 = tmp.indexOf(" ");
											if(index2==-1) {
												break;
											}
											else {
												tmpBuffer.append(tmp.substring(0,index2)+"\n");
												tmp = tmp.substring(index2+1);
											}
										}
										else {
											tmpBuffer.append(tmp.substring(0,index2)+"\n");
											tmp = tmp.substring(index2+1);
										}
									}
									if(tmp.trim().length()>0) {
										tmpBuffer.append(tmp+"\n");
									}
								}
							}
							message = tmpBuffer.toString();
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
					message = encode(message);
					for(int x=0;x<message.length();x++) {
						int c = (int)message.charAt(x);
						if(c>127) {
							message = message.substring(0,x)+"&#"+c+";"+message.substring(x+1);
						}
					}
					savePaste(id,user,message);
					content.append("<B>Your message has been stored and a message sent to the channel.</B> You can also copy this URL to access your paste: <A HREF='http://"+httpHost+":"+httpPort+"/paste?id="+id+"'>http://"+httpHost+":"+httpPort+"/paste?id="+id+"</A><P>");
					content.append(writeFooter());
					out.append("Content-Length: "+content.length()+"\r\n\r\n");
					
					try {
						httpOut.write(out.toString().getBytes());
						httpOut.write(content.toString().getBytes());
						httpOut.flush();
					}
					catch(Exception e) {;}


					writeMsg(myChan,BOLD+""+UNDERLINE+user+UNDERLINE+" has pasted text to the channel"+BOLD+": http://"+httpHost+":"+httpPort+"/paste?id="+id);
					return;
				}
			}
			else if(cgi.containsKey("id")) {
				String message = getPaste((String)cgi.get("id"));
				/*				
								for(int x=0;x<pastes.size();x++) {
								Pair p = (Pair)pastes.elementAt(x);
								if(((String)p.first()).equals((String)cgi.get("id"))) {
								message = (String)p.second();
								break;
								}
								}
				*/
				content.append("<HTML><HEAD><TITLE>");
				content.append(myNick);
				content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Paste:</H1><P> ");

				if(message==null) {
					Vector allPastes = listPastes();
					for(int x=0;x<allPastes.size();x++) {
						Pair p = (Pair)allPastes.elementAt(x);
						String timestamp = (String)p.first();
						String username = (String)p.second();
						content.append("<A HREF=/paste?id=");
						content.append(timestamp);
						content.append(">");
						content.append(timestamp);
						content.append("</A> by ");
						content.append(username);
						content.append(" at ");
						long time = Long.valueOf(timestamp.substring(0,13)).longValue();
						Date d = new Date(time);
						content.append(dateFormat.format(d));
						content.append("<BR>");
					}
					content.append(writeFooter());
				}
				else {
					content.append("<PRE>"+message+"</PRE>");
					content.append(writeFooter());
				}
			}
			else {
				content.append("<HTML><HEAD><TITLE>");
				content.append(myNick);
				content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Paste:</H1><P> ");
				content.append("<FORM METHOD=POST ACTION=/paste>Your Username: <INPUT TYPE=TEXT NAME=user SIZE=15> Auto-Break long lines: <INPUT TYPE=CHECKBOX NAME=format VALUE=yes CHECKED><BR><TEXTAREA NAME=message COLS=80 ROWS=18 WRAP=SOFT></TEXTAREA><BR><INPUT TYPE=SUBMIT NAME=Paste VALUE=Paste><P>");
				content.append(writeFooter());
			}

			out.append("Content-Length: "+content.length()+"\r\n\r\n");
			
			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception e) {;}
		}

		public void writeFortune(String params)
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");

			String fortuneCommand = fortuneProgram;
			if(fortuneCommand.indexOf("-s")!=-1) {
				fortuneCommand = fortuneCommand.substring(0,fortuneCommand.indexOf("-s"))+fortuneCommand.substring(fortuneCommand.indexOf("-s")+2);
			}
			ExecuteCommand command = new ExecuteCommand(fortuneCommand);
			StringBuffer output = new StringBuffer();
			command.processOut = output;
			command.finishedWaitTime = (200 + r.nextInt(100));
			command.execute();
			String fortune = output.toString();
			int index = fortune.indexOf("\n");
			while(index!=-1) {
				fortune = fortune.substring(0,index)+"<BR>"+fortune.substring(index+1);
				index = fortune.indexOf("\n");
			}

			content.append("<HTML><HEAD><TITLE>");
			content.append(myNick);
			content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Fortune:</H1><P> ");
			content.append("<PRE>"+fortune+"</PRE><P>");
			content.append(writeFooter());

			out.append("Content-Length: "+content.length()+"\r\n\r\n");
			
			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception e) {;}
		}

		public void writeUser(String params)
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");
			Hashtable cgi = parseParams(params);
			if(cgi.containsKey("user")) {
				String user = ((String)cgi.get("user")).toLowerCase();
				content.append("<HTML><HEAD><TITLE>");
				content.append(myNick);
				content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>User Statistics for "+user+"</H1><P> ");
				content.append("<TABLE>");
				if(channelStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel</TD><TD>"+channelStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel</TD><TD>none</TD></TR>");
				}
				if(channelCapsStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel that were in all CAPS</TD><TD>"+channelCapsStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel that were in all CAPS</TD><TD>none</TD></TR>");
				}
				if(channelLinksStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel that contained a link</TD><TD>"+channelLinksStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel that contained a link</TD><TD>none</TD></TR>");
				}
				if(channelShortStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel that were less than 5 letters long</TD><TD>"+channelShortStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel that were less than 5 letters long</TD><TD>none</TD></TR>");
				}
				if(channelLongStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel that were more than 150 letters long</TD><TD>"+channelLongStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel that were more than 150 letters long</TD><TD>none</TD></TR>");
				}
				if(channelQuestionStats.containsKey(user)) {
					content.append("<TR><TD>Number of messages sent to channel that were questions</TD><TD>"+channelQuestionStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of messages sent to channel that were questions</TD><TD>none</TD></TR>");
				}
				if(channelEmoticonStats.containsKey(user)) {
					content.append("<TR><TD>Number of emoticons used in the channel</TD><TD>"+channelEmoticonStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of emoticons used in the channel</TD><TD>none</TD></TR>");
				}
				if(channelJoinStats.containsKey(user)) {
					content.append("<TR><TD>Number of joins into the channel</TD><TD>"+channelJoinStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of joins into the channel</TD><TD>none</TD></TR>");
				}
				if(msgStats.containsKey(user)) {
					content.append("<TR><TD>Number of /msg's sent to me</TD><TD>"+msgStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of /msg's sent to me</TD><TD>none</TD></TR>");
				}
				if(rollStats.containsKey(user)) {
					content.append("<TR><TD>Number of dice rolled</TD><TD>"+rollStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of dice rolled</TD><TD>none</TD></TR>");
				}
				if(ballStats.containsKey(user)) {
					content.append("<TR><TD>Number of 8-Ball predictions</TD><TD>"+ballStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of 8-Ball predictions</TD><TD>none</TD></TR>");
				}
				if(triviaStats.containsKey(user)) {
					content.append("<TR><TD>Number of trivia requested</TD><TD>"+triviaStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of trivia requested</TD><TD>none</TD></TR>");
				}
				if(answerStats.containsKey(user)) {
					content.append("<TR><TD>Number of trivia answers requested</TD><TD>"+answerStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of trivia answers requested</TD><TD>none</TD></TR>");
				}
				if(gameStats.containsKey(user)) {
					content.append("<TR><TD>Number of games requested</TD><TD>"+gameStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of games requested</TD><TD>none</TD></TR>");
				}
				if(fortuneStats.containsKey(user)) {
					content.append("<TR><TD>Number of fortunes requested</TD><TD>"+fortuneStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of fortunes requested</TD><TD>none</TD></TR>");
				}
				if(youtubeStats.containsKey(user)) {
					content.append("<TR><TD>Number of youtube videos requested</TD><TD>"+youtubeStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of youtube videos requested</TD><TD>none</TD></TR>");
				}
				if(pantsuStats.containsKey(user)) {
					content.append("<TR><TD>Number of pantsu images requested</TD><TD>"+pantsuStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of pantsu images requested</TD><TD>none</TD></TR>");
				}
				if(boobiesStats.containsKey(user)) {
					content.append("<TR><TD>Number of boobies images requested</TD><TD>"+boobiesStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of boobies images requested</TD><TD>none</TD></TR>");
				}
				if(scriptStats.containsKey(user)) {
					content.append("<TR><TD>Number of script searches</TD><TD>"+scriptStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of script searches</TD><TD>none</TD></TR>");
				}
				if(responsesStats.containsKey(user)) {
					content.append("<TR><TD>Number of responses added</TD><TD>"+responsesStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of responses added</TD><TD>none</TD></TR>");
				}
				if(triggerStats.containsKey(user)) {
					content.append("<TR><TD>Number of times I was triggered</TD><TD>"+triggerStats.get(user)+"</TD></TR>");
				}
				else {
					content.append("<TR><TD>Number of times I was triggered</TD><TD>none</TD></TR>");
				}
				int lolis = loliTable.getLolis(user.toLowerCase());
				content.append("<TR><TD>Number of lolis obtained</TD><TD>"+lolis+"</TD></TR>");
				Pair p = (Pair)rpsStats.get(user);
				if(p!=null) {
					if(p.first().equals("0")) {
						content.append("<TR><TD>Number of times you beat me in Rock/Paper/Scissors</TD><TD>none</TD></TR>");
					}
					else {
						content.append("<TR><TD>Number of times you beat me in Rock/Paper/Scissors</TD><TD>"+p.first()+"</TD></TR>");
					}
					if(p.second().equals("0")) {
						content.append("<TR><TD>Number of times I beat you in Rock/Paper/Scissors</TD><TD>none</TD></TR>");
					}
					else {
						content.append("<TR><TD>Number of times I beat you in Rock/Paper/Scissors</TD><TD>"+p.second()+"</TD></TR>");
					}
				}
				else {
					content.append("<TR><TD>Number of times you beat me in Rock/Paper/Scissors</TD><TD>none</TD></TR>");
					content.append("<TR><TD>Number of times I beat you in Rock/Paper/Scissors</TD><TD>none</TD></TR>");
				}

				p = (Pair)worthTable.get(user);
				if(p!=null) {
					String worthString = p.second().toString();
					worthString = worthString.substring(0,worthString.length()-2)+"."+worthString.substring(worthString.length()-2);
					content.append("<TR><TD>Your net self worth</TD><TD>$"+worthString+"</TD></TR>");
				}				
				
				content.append("</TABLE></P>");
				content.append(writeFooter());
			}
			else {
				content.append("<HTML><HEAD><TITLE>");
				content.append(myNick);
				content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>User Statistics</H1><P> ");
				Enumeration e = channelStats.keys();
				Vector unsorted = new Vector();
				while(e.hasMoreElements()) {
					unsorted.addElement(new Pair(e.nextElement(),""));
				}
				Vector sorted = StringTools.pairSort(unsorted);
				for(int x=0;x<sorted.size();x++) {
					String user = (String)((Pair)sorted.elementAt(x)).first();
					content.append("<A HREF=/user?user=");
					content.append(user);
					content.append(">");
					content.append(user);
					content.append("</A><BR>");
				}
				content.append(writeFooter());
			}

			out.append("Content-Length: "+content.length()+"\r\n\r\n");
			
			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception e) {;}
		}


		public void writeChannel(String params)
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");

			content.append("<HTML><HEAD><TITLE>");
			content.append(myNick);
			content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Channel Statistics</H1><P> ");
			content.append("<H2>Channel statistics since ");
			content.append(channelStatsDate);
			content.append(":</H2><TABLE WIDTH=90% CELLSPACING=0 CELLPADDING=3><TR>");

			content.append("<TD COLSPAN=9><B>Number of messages sent to the channel by user</B></TD></TR>");
			Enumeration e = channelStats.keys();
			StringBuffer buffer = new StringBuffer();
			Vector v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			boolean sorted = false;
			Pair[] list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			boolean blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("&nbsp;&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("&nbsp;&nbsp;&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}

			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of messages sent to the channel in all CAPS:</B></TD></TR>");
			if(lastCaps!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastCaps+"</TD></TR>");
			}
			e = channelCapsStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelCapsStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}

			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of links sent to the channel:</B></TD></TR>");
			if(lastLinks!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastLinks+"</TD></TR>");
			}
			e = channelLinksStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelLinksStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of short messages sent to the channel (5 letters or less):</B></TD></TR>");
			if(lastShort!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastShort+"</TD></TR>");
			}
			e = channelShortStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelShortStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of long messages sent to the channel (150 letters or more):</B></TD></TR>");
			if(lastLong!=null) {
				String tmpLong = "";
				int index = 0;
				int index2 = 100;
				String tmp = lastLong.substring(index,index2);
				while(true) {
					if(tmp.lastIndexOf(" ")!=-1) {
						index2 = tmp.lastIndexOf(" ")+index;
					}
					tmpLong = tmpLong+lastLong.substring(index,index2)+"<BR>";
					index = index2+1;
					index2 = index+100;
					if(index2>=lastLong.length()) {
						tmpLong = tmpLong+lastLong.substring(index);
						break;
					}
					tmp = lastLong.substring(index,index2);
				}
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+tmpLong+"</TD></TR>");
			}
			e = channelLongStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelLongStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of questions sent to the channel:</B></TD></TR>");
			if(lastQuestion!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastQuestion+"</TD></TR>");
			}
			e = channelQuestionStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelQuestionStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of emoticons used in the channel:</B></TD></TR>");
			if(lastEmoticon!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastEmoticon+"</TD></TR>");
			}
			e = channelEmoticonStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelEmoticonStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#9999CC>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("</TD><TD ALIGN=LEFT BGCOLOR=#99CC99>(");
					try {
						int numerator = Integer.parseInt(list[x].first().toString());
						int denominator = Integer.parseInt((String)channelStats.get(list[x].second().toString()));
						int percent1 = (int)(((double)numerator/denominator)*100);
						int percent2 = (int)(((double)numerator/denominator)*1000)%10;
						content.append(percent1+"."+percent2);
					}
					catch(Exception eshit) {;}
					content.append("%)");
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Number of times someone joined the channel:</B></TD></TR>");
			if(lastJoin!=null) {
				content.append("<TR><TD COLSPAN=9><B>Most Recent:</B> "+lastJoin+"</TD></TR>");
			}
			e = channelJoinStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)channelJoinStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}

			if(loliMessage!=null) {
				content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>A User with Loli has left this message:</B></TD></TR>");
				content.append("<TR><TD COLSPAN=9>"+loliMessage+"</TD></TR>");
			}
			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Users with the most Lolis</B></TD></TR>");
			String[] loliUsers = loliTable.getUsers();
			blue = false;
			for(int x=0;x<loliUsers.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(loliUsers[x]);
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(loliTable.getLolis(loliUsers[x])+"&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(loliUsers[x]);
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(loliTable.getLolis(loliUsers[x])+"&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}

			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Net Self Worth:</B></TD></TR>");
			e = worthTable.keys();
			v = new Vector();
			while(e.hasMoreElements()) {
				String user = (String)e.nextElement();
				Pair p = (Pair)worthTable.get(user);
				p = new Pair(p.second(),user);
				v.addElement(p);
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Long.valueOf((String)list[x].first()).longValue() < Long.valueOf((String)list[x+1].first()).longValue()) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				String worthString = list[x].first().toString();
				worthString = worthString.substring(0,worthString.length()-2)+"."+worthString.substring(worthString.length()-2);
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#9999CC>$");
					content.append(worthString);
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#99CC99>$");
					content.append(worthString);
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}


			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Most popular FORTUNE databases:</B></TD></TR>");
			e = fortuneDatabaseStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)fortuneDatabaseStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Integer.parseInt((String)list[x].first()) < Integer.parseInt((String)list[x+1].first())) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			blue = false;
			for(int x=0;x<list.length;x++) {
				if(x%3==0) {
					blue = !blue;
					content.append("<TR>");
				}
				if(blue) {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAAAFF>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#9999CC>");
					content.append(list[x].first().toString());
					content.append("&nbsp;</TD>");
				}
				else {
					content.append("<TD ALIGN=LEFT BGCOLOR=#AAFFAA>&nbsp;");
					content.append(list[x].second().toString());
					content.append("</TD><TD COLSPAN=2 ALIGN=LEFT BGCOLOR=#99CC99>");
					content.append(list[x].first().toString());
					content.append("&nbsp;</TD>");
				}
			}
			if(list.length%3==2) {
				content.append("<TD COLSPAN=3>&nbsp;</TD></TR>");
			}
			else if(list.length%3==1) {
				content.append("<TD COLSPAN=6>&nbsp;</TD></TR>");
			}
			else {
				content.append("</TR>");
			}

			content.append("<TR><TD COLSPAN=9>&nbsp;</TD></TR><TR><TD COLSPAN=9><B>Current idle-masters of the channel:</B></TD></TR>");
			e = idleStats.keys();
			buffer = new StringBuffer();
			v = new Vector();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String statCount = (String)idleStats.get(key);
				v.addElement(new Pair(statCount,key));
			}
			sorted = false;
			list = new Pair[v.size()];
			for(int x=0;x<list.length;x++) {
				list[x] = (Pair)v.elementAt(x);
			}
			while(!sorted) {
				sorted = true;
				for(int x=0;x<list.length-1;x++) {
					if(Long.valueOf((String)list[x].first()).longValue() > Long.valueOf((String)list[x+1].first()).longValue()) {
						Pair tmp = list[x];
						list[x] = list[x+1];
						list[x+1] = tmp;
						sorted = false;
					}
				}
			}
			for(int x=0;x<list.length;x++) {
				StringBuffer idleTime = new StringBuffer();
				long timestmp = (System.currentTimeMillis() - Long.valueOf((String)list[x].first()).longValue());
				timestmp = (long)(timestmp/1000);
				int seconds = (int)(timestmp % 60);
				timestmp = (long)(timestmp / 60);
				int minutes = (int)(timestmp % 60);
				timestmp = (long)(timestmp / 60);
				int hours = (int)(timestmp % 24);
				timestmp = (long)(timestmp / 24);

				if(timestmp>0) {
					idleTime.append(timestmp+" days, ");
				}
				if(hours>0) {
					idleTime.append(hours+" hours, ");
				}
				if(minutes>0) {
					idleTime.append(minutes+" minutes, ");
				}
				idleTime.append(seconds+" seconds");

				content.append("<TR><TD>"+list[x].second()+"</TD><TD COLSPAN=8>"+idleTime+"</TD></TR>");
			}

			SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, HH:mm");
			content.append("</TABLE><P>Most users in the channel: "+mostInChannel+" (on "+format.format(new Date(lastMostInChannel))+")<BR>");

			int maxRPSWin = 0;
			String maxWinUser = null;
			int maxRPSLose = 0;
			String maxLoseUser = null;

			e = rpsStats.keys();
			while(e.hasMoreElements()) {
				String rpsuser = (String)e.nextElement();
				Pair p = (Pair) rpsStats.get(rpsuser);
				int wins = Integer.parseInt(p.first()+"");
				int loses = Integer.parseInt(p.second()+"");
				if(wins>maxRPSWin) {
					maxRPSWin = wins;
					maxWinUser = rpsuser;
				}
				if(loses>maxRPSLose) {
					maxRPSLose = loses;
					maxLoseUser = rpsuser;
				}
			}

			if(maxWinUser!=null) {
				content.append("<B>"+maxWinUser+"</B> has beaten me the most times ("+maxRPSWin+") at Rock/Paper/Scissors<BR>");
			}
			if(maxLoseUser!=null) {
				content.append("<B>"+maxLoseUser+"</B> has lost to me the most times ("+maxRPSLose+") at Rock/Paper/Scissors<BR>");
			}

			content.append("Number of times '...' has been used in this channel: "+dotStats+"<BR>");
			int count = 0;
			e = scriptStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)scriptStats.get(e.nextElement())).trim());
			}
			content.append("Number of Evangelion script searches: "+count+"<BR>");
			count = 0;
			e = triggerStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)triggerStats.get(e.nextElement())).trim());
			}
			content.append("Number of times "+myNick+" has been triggered: "+count+"<BR>");
			count = 0;
			e = responsesStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)responsesStats.get(e.nextElement())).trim());
			}
			content.append("Number of responses added: "+count+"<BR>");
			count = 0;
			e = fortuneStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)fortuneStats.get(e.nextElement())).trim());
			}
			content.append("Number of FORTUNE requests: "+count+"<BR>");
			count = 0;
			e = gameStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)gameStats.get(e.nextElement())).trim());
			}
			content.append("Number of GAME requests: "+count+"<BR>");
			count = 0;
			e = triviaStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)triviaStats.get(e.nextElement())).trim());
			}
			content.append("Number of TRIVIA requests: "+count+"<BR>");
			count = 0;
			e = answerStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)answerStats.get(e.nextElement())).trim());
			}
			content.append("Number of TRIVIA answer requests: "+count+"<BR>");
			count = 0;
			e = rollStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)rollStats.get(e.nextElement())).trim());
			}
			content.append("Number of dice ROLLed: "+count+"<BR>");
			count = 0;
			e = youtubeStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)youtubeStats.get(e.nextElement())).trim());
			}
			content.append("Number of YOUTUBE video requests: "+count+"<BR>");
			count = 0;
			e = pantsuStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)pantsuStats.get(e.nextElement())).trim());
			}
			content.append("Number of PANTSU images requests: "+count+"<BR>");
			count = 0;
			e = boobiesStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)boobiesStats.get(e.nextElement())).trim());
			}
			content.append("Number of BOOBIES images requests: "+count+"<BR>");
			count = 0;
			e = ballStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)ballStats.get(e.nextElement())).trim());
			}
			content.append("Number of 8-Ball predictions: "+count+"<BR>");
			count = 0;
			e = msgStats.keys();
			while(e.hasMoreElements()) {
				count = count+Integer.parseInt(((String)msgStats.get(e.nextElement())).trim());
			}
			content.append("Number of times "+myNick+" has been /msg'ed: "+count+"<BR>");
			content.append(writeFooter());
			
			out.append("Content-Length: "+content.length()+"\r\n\r\n");

			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception ex) {;}
		}

		public void writeRecent(String params)
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");

			content.append("<HTML><HEAD><META NAME=ROBOTS CONTENT=NOARCHIVE><TITLE>");
			content.append(myNick);
			content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>Recent Channel Activity</H1><P> ");
			content.append("<PRE>");
			synchronized(recent) {
				for(int x=0;x<recent.size();x++) {
					Pair p = (Pair)recent.elementAt(x);
					Pair p2 = (Pair)p.first();
					String user = encode((String)p2.first());
					String message = encode((String)p.second());
					if(params!=null && params.trim().length()>0) {
						if(recentFilters.containsKey("*")) {
							Vector filters = (Vector)recentFilters.get("*");
							for(int z=0;z<filters.size();z++) {
								Pair filter = (Pair)filters.elementAt(z);
								String first = (String)filter.first();
								String second = (String)filter.second();
								if(first.startsWith("~")) {
									first = first.substring(1);
									message = message.replace(" "+first+" "," "+second+" ");
									message = message.replace(" "+first+","," "+second+",");
									message = message.replace(" "+first+"."," "+second+".");
									if(message.startsWith(first+" ")) {
										message = second+" "+message.substring(first.length()+1);
									}
									try {
										if(message.indexOf(" "+first)==message.length()-(first.length()+1)) {
											message = message.substring(0,message.length()-(first.length()+1))+" "+second;
										}
									}
									catch(Exception e) {;}
								}
								else if(first.startsWith("@")) {
									first = first.substring(1);
									if(((String)p2.first()).equals(first)) {
										user = encode(second);
									}
								}
								else {
									message = message.replace(first,second);
								}
							}
						}
						if(recentFilters.containsKey(params)) {
							Vector filters = (Vector)recentFilters.get(params);
							for(int z=0;z<filters.size();z++) {
								Pair filter = (Pair)filters.elementAt(z);
								String first = (String)filter.first();
								String second = (String)filter.second();
								if(first.startsWith("~")) {
									first = first.substring(1);
									message = message.replace(" "+first+" "," "+second+" ");
									message = message.replace(" "+first+","," "+second+",");
									message = message.replace(" "+first+"."," "+second+".");
									if(message.startsWith(first+" ")) {
										message = second+" "+message.substring(first.length()+1);
									}
									try {
										if(message.indexOf(" "+first)==message.length()-(first.length()+1)) {
											message = message.substring(0,message.length()-(first.length()+1))+" "+second;
										}
									}
									catch(Exception e) {;}
								}
								else if(first.startsWith("@")) {
									if(((String)p2.first()).equals(first)) {
										user = encode(second);
									}
								}
								else {
									message = message.replace(first,second);
								}
							}
						}
						//						message = message.replace("V",params);
					}
					boolean boldOn = false;
					boolean underlineOn = false;
					StringBuffer tmp = new StringBuffer();
					for(int z=0;z<message.length();z++) {
						char c = message.charAt(z);
						if(c==BOLD) {
							if(boldOn) {
								tmp.append("</B>");
								boldOn = false;
							}
							else {
								tmp.append("<B>");
								boldOn = true;
							}
						}
						else if(c==UNDERLINE) {
							if(underlineOn) {
								tmp.append("</U>");
								underlineOn = false;
							}
							else {
								tmp.append("<U>");
								underlineOn = true;
							}
						}
						else {
							tmp.append(c);
						}
					}
					if(underlineOn) {
						tmp.append("</U>");
					}
					if(boldOn) {
						tmp.append("</B>");
					}
					try {
						message = removeFormatting(tmp.toString());
					}
					catch(Exception e) {;}

					if(message.trim().startsWith("ACTION")) {
						content.append("<FONT COLOR=#000088>* ");
						content.append(user);
						content.append(message.trim().substring(6));
						content.append("</FONT>");
					}
					else if(user.equals(" JOIN")) {
						content.append("<FONT COLOR=#006600>*** ");
						content.append(message);
						content.append(" joined ");
						content.append(myChan);
						content.append("</FONT>");
					}
					else if(user.equals(" QUIT")) {
						content.append("<FONT COLOR=#880000>*** ");
						content.append(message);
						content.append(" left ");
						content.append(myChan);
						content.append("</FONT>");
					}
					else if(user.startsWith(" NICK-")) {
						content.append("<FONT COLOR=#333333>*** ");
						content.append(user.substring(6));
						content.append(" is now known as ");
						content.append(message);
						content.append("</FONT>");
					}
					else {
						content.append("<FONT COLOR=#880088>&lt;</FONT><FONT COLOR=#008888>");
						content.append(user);
						content.append("</FONT><FONT COLOR=#880088>&gt;</FONT> ");
						int index = message.indexOf("http://");
						if(index!=-1) {
							String first = message.substring(0,index);
							int index2 = message.indexOf(" ",index);
							if(index2==-1) {
								index2 = message.length();
							}
							String url = message.substring(index,index2);
							if(index2==message.length()) {
								message = "";
							}
							else {
								message = message.substring(index2);
							}
							content.append(first);
							content.append("<A HREF='");
							content.append(url);
							content.append("'>");
							content.append(url);
							content.append("</A>");
							content.append(message);
						}
						else {
							content.append(message);
						}
					}
					content.append("\n");				
				}
			}
			content.append("</PRE>");
			content.append(writeFooter());
			out.append("Content-Length: "+content.length()+"\r\n\r\n");

			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception ex) {;}
		}

		public String writeFooter()
		{
			//			return("<HR><A HREF=/>MAIN</A> - <A HREF=/paste>Paste</A> - <A HREF=/paste?id=0>All Pastes</A> - <A HREF=/channel>Channel Stats</A> - <A HREF=/user>User Stats</A> - <A HREF=/recent>Recent Activity</A> - <A HREF=/fortune>Fortune</A> - <A HREF=/rss.xml>RSS Feed (Sort of)</A></BODY></HTML>");
			return("<HR><A HREF=/>MAIN</A> - <A HREF=/paste>Paste</A> - <A HREF=/paste?id=0>All Pastes</A> - <A HREF=/channel>Channel Stats</A> - <A HREF=/user>User Stats</A> - <A HREF=/recent>Recent Activity</A> - <A HREF=/fortune>Fortune</A></BODY></HTML>");
		}
			
		public void writeMainPage()
		{
			StringBuffer out = new StringBuffer();
			StringBuffer content = new StringBuffer();
			out.append("HTTP/1.1 200 Ok\r\n");
			out.append("Connection: close\r\n");
			out.append("Content-Type: text/html\r\n");

			content.append("<HTML><HEAD><TITLE>");
			content.append(myNick);
			content.append(" Bot @ #egf, by Ornette</TITLE></HEAD><BODY><H1>I'm the ");
			content.append(myNick);
			content.append(" Bot @ #egf, What can I do for you?</H1><P><UL>");
			content.append("<LI><A HREF=/paste>Create a Paste</A>");
			content.append("<LI><A HREF=/channel>View the Channel Statistics</A>");
			content.append("<LI><A HREF=/user>View User Statistics</A>");
			content.append("<LI><A HREF=/recent>Recent Channel Activity</A>");
			content.append("<LI><A HREF=/fortune>Give me a random fortune</A>");
			//			content.append("<LI><A HREF=/rss.xml>RSS News Feed (Sort of)</A>");
			content.append("</UL></BODY></HTML>");

			out.append("Content-Length: "+content.length()+"\r\n\r\n");

			try {
				httpOut.write(out.toString().getBytes());
				httpOut.write(content.toString().getBytes());
				httpOut.flush();
			}
			catch(Exception e) {;}
		}
	}


	class NickHash
	{
		public Vector nicks;

		public NickHash()
		{
			nicks = new Vector();
		}

		public synchronized boolean containsKey(String o)
		{
			boolean rval = false;
			for(int x=0;x<nicks.size();x++) {
				Pair p = (Pair)nicks.elementAt(x);
				String key = (String)p.first();
				if(o.matches(key)) {
					rval = true;
					break;
				}
			}
			return(rval);
		}

		public synchronized String get(String o)
		{
			String rval = null;
			for(int x=0;x<nicks.size();x++) {
				Pair p = (Pair)nicks.elementAt(x);
				String key = (String)p.first();
				if(o.matches(key)) {
					rval = (String)p.second();
					break;
				}
			}
			return(rval);
		}

		public synchronized Enumeration keys()
		{
			Vector v = new Vector();
			for(int x=0;x<nicks.size();x++) {
				Pair p = (Pair)nicks.elementAt(x);
				v.addElement(p.first());
			}
			return(v.elements());
		}

		public synchronized String put(String key, String value)
		{
			String rval = null;
			for(int x=0;x<nicks.size();x++) {
				Pair p = (Pair)nicks.elementAt(x);
				if(p.first().toString().equals(key)) {
					rval = (String)p.second();
					p.setSecond(value);
					break;
				}
			}
			if(rval==null) {
				nicks.addElement(new Pair(key,value));
			}
			return(rval);
		}

		public synchronized String remove(String key)
		{
			String rval = null;
			for(int x=0;x<nicks.size();x++) {
				Pair p = (Pair)nicks.elementAt(x);
				if(p.first().toString().equals(key)) {
					rval = (String)p.second();
					nicks.remove(p);
					break;
				}
			}
			return(rval);
		}
	}

	public void newRSSArticles(String title, String url)
	{
		writeMsg(myChan,BOLD+title+BOLD+" - "+HTTPClient.unconvert(url));
		delay(5000,3000);
		writeMsg("Ornette",BOLD+title+BOLD+" - "+HTTPClient.unconvert(url));
		lastRSSURL = HTTPClient.unconvert(url);
	}


	/*
	  class RSSThread extends Thread
	  {
	  String url;
	  Vector latestStories;
	  Date lastUpdate;
	  //		int lastSize;
	  boolean checkMatches;
	  HTTPLoader myLoader;

	  public RSSThread(String rssurl)
	  {
	  url = rssurl;
	  lastUpdate = new Date();
	  latestStories = new Vector();
	  checkMatches = true;
	  myLoader = new HTTPLoader();
	  myLoader.setAllowForeignCookies(true);
	  myLoader.charset = "UTF-8";
	  setName("RSS Thread "+rssurl);
	  start();
	  }

	  public RSSThread(String rssurl, boolean check)
	  {
	  url = rssurl;
	  lastUpdate = new Date();
	  latestStories = new Vector();
	  checkMatches = check;
	  myLoader = new HTTPLoader();
	  myLoader.setAllowForeignCookies(true);
	  myLoader.charset = "UTF-8";
	  setName("RSS Thread "+rssurl);
	  start();
	  }


	  public void log(String s)
	  {
	  try {
	  StringBuilder logString = new StringBuilder();
	  logString.append((new Date()).toString());
	  logString.append(url);
	  logString.append(" ("+checkMatches+")");
	  logString.append("["+latestStories.size()+"]  ");
	  logString.append(s);
	  logString.append("\n");
	  rssLog.write(logString.toString());
	  rssLog.flush();
	  }
	  catch(Exception e) {
	  e.printStackTrace();
	  }
	  }
		
	  public void run()
	  {
	  boolean first = true;
	  while(true) {
	  try {
	  log("Getting RSS - first="+first);
	  String page = myLoader.getURL(url);
	  if(page.trim().startsWith("<?xml")) {
	  log("RSS is xml");
	  Vector results = parseRSS(page);
	  System.out.println("Loading '"+url+"' with "+results.size()+" results");
	  if(!first) {
	  //							if(results.size()>3) {
	  if(results.size()>0) {
	  Vector newResults = new Vector();
	  //								if(!checkMatches) {
	  for(int x=0;x<results.size();x++) {
	  String rssAtom = (String)results.elementAt(x);
	  int index = rssAtom.indexOf("<link>");
	  if(index!=-1) {
	  try {
	  String link = rssAtom.substring(index+6);
	  index = link.indexOf("</link>");
	  link = link.substring(0,index);
	  boolean addResult = true;
	  for(int z=0;z<latestStories.size();z++) {
	  String resultAtom = (String)latestStories.elementAt(z);
	  if(resultAtom.indexOf(link)!=-1) {
	  addResult = false;
	  break;
	  }
	  }
	  if(addResult) {
	  //											System.out.println(link);
	  log("Found new article: "+link);
	  newResults.addElement(rssAtom);
	  }
	  }
	  catch(Exception e) {
	  System.out.println("Couldn't find link: "+rssAtom);
	  e.printStackTrace();
	  }
	  }
	  }
	  lastUpdate = new Date();

	  int count = 0;
	  for(int x=0;x<newResults.size();x++) {
	  String rssAtom = (String)newResults.elementAt(x);
	  int index = rssAtom.indexOf("<title>");
	  if(index!=-1) {
	  String title = rssAtom.substring(index+7);
	  index = title.indexOf("</title");
	  title = title.substring(0,index);
	  index = rssAtom.indexOf("<link>");
	  if(index!=-1) {
	  String url = rssAtom.substring(index+6);
	  index = url.indexOf("</link>");
	  url = url.substring(0,index);
	  if(!url.trim().toLowerCase().equals(lastRSSURL.trim().toLowerCase())) {
	  if(count<3) {
	  writeMsg(myChan,BOLD+title+BOLD+" - "+HTTPClient.unconvert(url));
	  delay(5000,3000);
	  writeMsg("Ornette",BOLD+title+BOLD+" - "+HTTPClient.unconvert(url));
	  lastRSSURL = HTTPClient.unconvert(url);
	  }
	  count++;
	  latestStories.addElement(rssAtom);
	  if(latestStories.size()>100) {
	  latestStories.removeElementAt(0);
	  }
	  }
	  }
	  else {
	  log("Strange... can't fine <link>");
	  }
	  }
	  else {
	  log("Strange... can't find <title>");
	  }
	  }
	  }
	  else if(latestStories.size()>0) {
	  log("No results, setting first to true");
	  first = true;
	  }
	  }
	  else if(checkMatches || results.size()>0) {
	  latestStories = results;
	  log("Did nothing, NOT FIRST, latestStories="+latestStories.size());
	  first = false;
	  }
	  //						sleep(Math.abs(r.nextInt()%600000)+1200000);
	  }						
	  else if(page.trim().length()>0) {
	  log("RSS is NOT xml, link might be broken");
	  // we got something but data is retarded, reload page quickly
	  //						sleep(25000);
	  }
	  //					else {
	  sleep(Math.abs(r.nextInt()%600000)+1200000);
	  //					}
	  }
	  catch(Exception e) {
	  e.printStackTrace();
	  }
	  }
	  }

	  public Vector parseRSS(String page)
	  {
	  Vector rval = new Vector();
	  int index = page.indexOf("<item>");
	  if(index==-1) {
	  index = page.indexOf("<item ");
	  }
	  while(index!=-1) {
	  //				log("Found <item ");
	  page = page.substring(index);
	  index = page.indexOf("</item>");
	  String tmp = page.substring(0,index+7);
	  String cleaned = cleanRSS(tmp);
	  page = page.substring(index+7);
	  if(page.indexOf("evalink.net")==-1 ||  
	  (cleaned.indexOf("<link>http://neweva.blog103.fc2.com")!=-1 &&
	  cleaned.indexOf("<link>http://eva2.0.b-ch.com")!=-1 &&
	  cleaned.indexOf("<link>http://evangeliwon.blog107.fc2.com")!=-1 &&
	  cleaned.indexOf("<link>http://curieux.blog40.fc2.com")!=-1 &&
	  cleaned.indexOf("<link>http://www.evastore.jp")!=-1 &&
	  cleaned.indexOf("<link>http://mainichi.jp")!=-1 &&
	  cleaned.indexOf("<link>http://akiba.kakaku.com")!=-1 &&
	  cleaned.indexOf("<link>http://ascii.jp")!=-1 &&
	  cleaned.indexOf("<link>http://av.watch.impress.co.jp")!=-1 &&
	  cleaned.indexOf("<link>http://www.i-mezzo.net")!=-1 &&
	  cleaned.indexOf("<link>http://feeds.journal.mycom.co.jp")!=-1 &&
	  cleaned.indexOf("<link>http://akibahobby.net")!=-1 )
	  ) {
	  if(checkMatches) {
	  for(int x=0;x<rssMatch.length;x++) {
	  if(cleaned.toLowerCase().indexOf(rssMatch[x])!=-1 || match(rssMatch[x],cleaned)) {
	  log("Found Match: "+rssMatch[x]);
	  rval.addElement(cleaned);
	  break;
	  }
	  }
	  }
	  else {
	  //						log("Adding Item");
	  rval.addElement(cleaned);
	  }
	  }
	  index = page.indexOf("<item>");
	  if(index==-1) {
	  index = page.indexOf("<item ");
	  }
	  }
	  log("Parsed "+rval.size()+" results");
	  return(rval);
	  }

	  public String cleanRSS(String rss)
	  {
	  StringBuffer rval = new StringBuffer();
	  rval.append("<item>\n");
	  try {
	  int index1 = rss.indexOf("<title>");
	  int index2 = rss.indexOf("</title>",index1);
	  rval.append(rss.substring(index1,index2+8));
	  rval.append("\n");

	  index1 = rss.indexOf("<link>");
	  index2 = rss.indexOf("</link>",index1);
	  String tmp = rss.substring(index1,index2+7);
	  if(tmp.indexOf("http://news.google.com/")!=-1) {
	  //					System.out.println("URL = "+tmp);
	  int tmpIndex = tmp.indexOf("url=");
	  if(tmpIndex!=-1) {
	  tmp = "<link>"+HTMLStringTools.decode(tmp.substring(tmpIndex+4,tmp.length()-7))+"</link>";
	  }
	  }
	  rval.append(tmp);
	  rval.append("\n");

	  index1 = rss.indexOf("<guid");
	  if(index1!=-1) {
	  index1 = rss.indexOf(">",index1);
	  index2 = rss.indexOf("</guid>",index1);
	  rval.append("<guid>");
	  rval.append(rss.substring(index1+1,index2+7));
	  rval.append("\n");
	  }

	  index1 = rss.indexOf("<pubDate>");
	  if(index1!=-1) {
	  index2 = rss.indexOf("</pubDate>",index1);
	  rval.append(rss.substring(index1,index2+10));
	  rval.append("\n");
	  }

	  //				index1 = rss.indexOf("<description>");
	  //				if(index1!=-1) {
	  //					index2 = rss.indexOf("</description>",index1);
	  //					rval.append(rss.substring(index1,index2+14));
	  //					rval.append("\n");
	  //				}

	  }
	  catch(Exception e) {e.printStackTrace();}
	  rval.append("</item>\n");

	  return(rval.toString());
	  }

	  public Vector intersect(Vector list1, Vector list2)
	  {
	  Vector rval = new Vector();

	  Vector titleList1 = new Vector();
	  Vector titleList2 = new Vector();

	  Vector realList1 = new Vector();
	  Vector realList2 = new Vector();

	  for(int x=0;x<list1.size();x++) {
	  String s = (String)list1.elementAt(x);
	  String realS = (String)list1.elementAt(x);
	  int index = s.indexOf("<title>");
	  if(index!=-1) {
	  s = s.substring(index+7);
	  index = s.indexOf("</title>");
	  if(index!=-1) {
	  s = s.substring(0,index);
	  titleList1.addElement(s);
	  realList1.addElement(realS);
	  }
	  }
	  }

	  for(int x=0;x<list2.size();x++) {
	  String s = (String)list2.elementAt(x);
	  String realS = (String)list2.elementAt(x);
	  int index = s.indexOf("<title>");
	  if(index!=-1) {
	  s = s.substring(index+7);
	  index = s.indexOf("</title>");
	  if(index!=-1) {
	  s = s.substring(0,index);
	  titleList2.addElement(s);
	  realList2.addElement(realS);
	  }
	  }
	  }

	  for(int x=0;x<titleList2.size();x++) {
	  if(!titleList1.contains((String)titleList2.elementAt(x))) {
	  rval.addElement(realList2.elementAt(x));
	  }
	  }


	  //			for(int x=0;x<list2.size();x++) {
	  //				if(!list1.contains((String)list2.elementAt(x))) {
	  //					rval.addElement(list2.elementAt(x));
	  //				}
	  //			}

	  return(rval);
	  }

	  public boolean match(String str1, String str2)
	  {
	  boolean rval = false;
	  try {
	  byte[] b = str1.getBytes("UTF-8");
	  byte[] pageBytes = str2.getBytes("UTF-8");
	  int index = 0;
	  while(index<pageBytes.length-b.length) {
	  if(pageBytes[index] == b[0]) {
	  boolean match = true;
	  for(int x=1;x<b.length;x++) {
	  if(pageBytes[index+x] != b[x]) {
	  match = false;
	  break;
	  }
	  }
	  if(match) {
	  rval = true;
	  break;
	  }
	  }
	  index++;
	  }
	  }
	  catch(Exception e) {;}
	  //			System.out.println("Matching: "+str1+" = "+rval);
	  return(rval);
	  }

	  }
	*/




















	static String[] unicode = {"\u3000","\u3001","\u3002","\u3003","\u3004","\u3005","\u3006","\u3007","\u3008","\u3009","\u300a","\u300b","\u300c","\u300e","\u300d","\u300f","\u3010","\u3011","\u3012","\u3013","\u3014","\u3015","\u3016","\u3017","\u3018","\u3019","\u301a","\u301b","\u301c","\u301e","\u301d","\u301f","\u3020","\u3021","\u3022","\u3023","\u3024","\u3025","\u3026","\u3027","\u3028","\u3029","\u302a","\u302b","\u302c","\u302e","\u302d","\u302f","\u3030","\u3031","\u3032","\u3033","\u3034","\u3035","\u3036","\u3037","\u3038","\u3039","\u303a","\u303b","\u303c","\u303e","\u303d","\u303f","\u3040","\u3041","\u3042","\u3043","\u3044","\u3045","\u3046","\u3047","\u3048","\u3049","\u304a","\u304b","\u304c","\u304e","\u304d","\u304f","\u3050","\u3051","\u3052","\u3053","\u3054","\u3055","\u3056","\u3057","\u3058","\u3059","\u305a","\u305b","\u305c","\u305e","\u305d","\u305f","\u3060","\u3061","\u3062","\u3063","\u3064","\u3065","\u3066","\u3067","\u3068","\u3069","\u306a","\u306b","\u306c","\u306e","\u306d","\u306f","\u3070","\u3071","\u3072","\u3073","\u3074","\u3075","\u3076","\u3077","\u3078","\u3079","\u307a","\u307b","\u307c","\u307e","\u307d","\u307f","\u3080","\u3081","\u3082","\u3083","\u3084","\u3085","\u3086","\u3087","\u3088","\u3089","\u308a","\u308b","\u308c","\u308e","\u308d","\u308f","\u3090","\u3091","\u3092","\u3093","\u3094","\u3095","\u3096","\u3097","\u3098","\u3099","\u309a","\u309b","\u309c","\u309e","\u309d","\u309f","\u30a0","\u30a1","\u30a2","\u30a3","\u30a4","\u30a5","\u30a6","\u30a7","\u30a8","\u30a9","\u30aa","\u30ab","\u30ac","\u30ae","\u30ad","\u30af","\u30b0","\u30b1","\u30b2","\u30b3","\u30b4","\u30b5","\u30b6","\u30b7","\u30b8","\u30b9","\u30ba","\u30bb","\u30bc","\u30be","\u30bd","\u30bf","\u30c0","\u30c1","\u30c2","\u30c3","\u30c4","\u30c5","\u30c6","\u30c7","\u30c8","\u30c9","\u30ca","\u30cb","\u30cc","\u30ce","\u30cd","\u30cf","\u30d0","\u30d1","\u30d2","\u30d3","\u30d4","\u30d5","\u30d6","\u30d7","\u30d8","\u30d9","\u30da","\u30db","\u30dc","\u30de","\u30dd","\u30df","\u30e0","\u30e1","\u30e2","\u30e3","\u30e4","\u30e5","\u30e6","\u30e7","\u30e8","\u30e9","\u30ea","\u30eb","\u30ec","\u30ee","\u30ed","\u30ef","\u30f0","\u30f1","\u30f2","\u30f3","\u30f4","\u30f5","\u30f6","\u30f7","\u30f8","\u30f9","\u30fa","\u30fb","\u30fc","\u30fe","\u30fd","\u30ff",
										"\u4e00","\u4e01","\u4e02","\u4e03","\u4e04","\u4e05","\u4e06","\u4e07","\u4e08","\u4e09","\u4e0a","\u4e0b","\u4e0c","\u4e0e","\u4e0d","\u4e0f","\u4e10","\u4e11","\u4e12","\u4e13","\u4e14","\u4e15","\u4e16","\u4e17","\u4e18","\u4e19","\u4e1a","\u4e1b","\u4e1c","\u4e1e","\u4e1d","\u4e1f","\u4e20","\u4e21","\u4e22","\u4e23","\u4e24","\u4e25","\u4e26","\u4e27","\u4e28","\u4e29","\u4e2a","\u4e2b","\u4e2c","\u4e2e","\u4e2d","\u4e2f","\u4e30","\u4e31","\u4e32","\u4e33","\u4e34","\u4e35","\u4e36","\u4e37","\u4e38","\u4e39","\u4e3a","\u4e3b","\u4e3c","\u4e3e","\u4e3d","\u4e3f","\u4e40","\u4e41","\u4e42","\u4e43","\u4e44","\u4e45","\u4e46","\u4e47","\u4e48","\u4e49","\u4e4a","\u4e4b","\u4e4c","\u4e4e","\u4e4d","\u4e4f","\u4e50","\u4e51","\u4e52","\u4e53","\u4e54","\u4e55","\u4e56","\u4e57","\u4e58","\u4e59","\u4e5a","\u4e5b","\u4e5c","\u4e5e","\u4e5d","\u4e5f","\u4e60","\u4e61","\u4e62","\u4e63","\u4e64","\u4e65","\u4e66","\u4e67","\u4e68","\u4e69","\u4e6a","\u4e6b","\u4e6c","\u4e6e","\u4e6d","\u4e6f","\u4e70","\u4e71","\u4e72","\u4e73","\u4e74","\u4e75","\u4e76","\u4e77","\u4e78","\u4e79","\u4e7a","\u4e7b","\u4e7c","\u4e7e","\u4e7d","\u4e7f","\u4e80","\u4e81","\u4e82","\u4e83","\u4e84","\u4e85","\u4e86","\u4e87","\u4e88","\u4e89","\u4e8a","\u4e8b","\u4e8c","\u4e8e","\u4e8d","\u4e8f","\u4e90","\u4e91","\u4e92","\u4e93","\u4e94","\u4e95","\u4e96","\u4e97","\u4e98","\u4e99","\u4e9a","\u4e9b","\u4e9c","\u4e9e","\u4e9d","\u4e9f","\u4ea0","\u4ea1","\u4ea2","\u4ea3","\u4ea4","\u4ea5","\u4ea6","\u4ea7","\u4ea8","\u4ea9","\u4eaa","\u4eab","\u4eac","\u4eae","\u4ead","\u4eaf","\u4eb0","\u4eb1","\u4eb2","\u4eb3","\u4eb4","\u4eb5","\u4eb6","\u4eb7","\u4eb8","\u4eb9","\u4eba","\u4ebb","\u4ebc","\u4ebe","\u4ebd","\u4ebf","\u4ec0","\u4ec1","\u4ec2","\u4ec3","\u4ec4","\u4ec5","\u4ec6","\u4ec7","\u4ec8","\u4ec9","\u4eca","\u4ecb","\u4ecc","\u4ece","\u4ecd","\u4ecf","\u4ed0","\u4ed1","\u4ed2","\u4ed3","\u4ed4","\u4ed5","\u4ed6","\u4ed7","\u4ed8","\u4ed9","\u4eda","\u4edb","\u4edc","\u4ede","\u4edd","\u4edf","\u4ee0","\u4ee1","\u4ee2","\u4ee3","\u4ee4","\u4ee5","\u4ee6","\u4ee7","\u4ee8","\u4ee9","\u4eea","\u4eeb","\u4eec","\u4eee","\u4eed","\u4eef","\u4ef0","\u4ef1","\u4ef2","\u4ef3","\u4ef4","\u4ef5","\u4ef6","\u4ef7","\u4ef8","\u4ef9","\u4efa","\u4efb","\u4efc","\u4efe","\u4efd","\u4eff",
										"\u4f00","\u4f01","\u4f02","\u4f03","\u4f04","\u4f05","\u4f06","\u4f07","\u4f08","\u4f09","\u4f0a","\u4f0b","\u4f0c","\u4f0e","\u4f0d","\u4f0f","\u4f10","\u4f11","\u4f12","\u4f13","\u4f14","\u4f15","\u4f16","\u4f17","\u4f18","\u4f19","\u4f1a","\u4f1b","\u4f1c","\u4f1e","\u4f1d","\u4f1f","\u4f20","\u4f21","\u4f22","\u4f23","\u4f24","\u4f25","\u4f26","\u4f27","\u4f28","\u4f29","\u4f2a","\u4f2b","\u4f2c","\u4f2e","\u4f2d","\u4f2f","\u4f30","\u4f31","\u4f32","\u4f33","\u4f34","\u4f35","\u4f36","\u4f37","\u4f38","\u4f39","\u4f3a","\u4f3b","\u4f3c","\u4f3e","\u4f3d","\u4f3f","\u4f40","\u4f41","\u4f42","\u4f43","\u4f44","\u4f45","\u4f46","\u4f47","\u4f48","\u4f49","\u4f4a","\u4f4b","\u4f4c","\u4f4e","\u4f4d","\u4f4f","\u4f50","\u4f51","\u4f52","\u4f53","\u4f54","\u4f55","\u4f56","\u4f57","\u4f58","\u4f59","\u4f5a","\u4f5b","\u4f5c","\u4f5e","\u4f5d","\u4f5f","\u4f60","\u4f61","\u4f62","\u4f63","\u4f64","\u4f65","\u4f66","\u4f67","\u4f68","\u4f69","\u4f6a","\u4f6b","\u4f6c","\u4f6e","\u4f6d","\u4f6f","\u4f70","\u4f71","\u4f72","\u4f73","\u4f74","\u4f75","\u4f76","\u4f77","\u4f78","\u4f79","\u4f7a","\u4f7b","\u4f7c","\u4f7e","\u4f7d","\u4f7f","\u4f80","\u4f81","\u4f82","\u4f83","\u4f84","\u4f85","\u4f86","\u4f87","\u4f88","\u4f89","\u4f8a","\u4f8b","\u4f8c","\u4f8e","\u4f8d","\u4f8f","\u4f90","\u4f91","\u4f92","\u4f93","\u4f94","\u4f95","\u4f96","\u4f97","\u4f98","\u4f99","\u4f9a","\u4f9b","\u4f9c","\u4f9e","\u4f9d","\u4f9f","\u4fa0","\u4fa1","\u4fa2","\u4fa3","\u4fa4","\u4fa5","\u4fa6","\u4fa7","\u4fa8","\u4fa9","\u4faa","\u4fab","\u4fac","\u4fae","\u4fad","\u4faf","\u4fb0","\u4fb1","\u4fb2","\u4fb3","\u4fb4","\u4fb5","\u4fb6","\u4fb7","\u4fb8","\u4fb9","\u4fba","\u4fbb","\u4fbc","\u4fbe","\u4fbd","\u4fbf","\u4fc0","\u4fc1","\u4fc2","\u4fc3","\u4fc4","\u4fc5","\u4fc6","\u4fc7","\u4fc8","\u4fc9","\u4fca","\u4fcb","\u4fcc","\u4fce","\u4fcd","\u4fcf","\u4fd0","\u4fd1","\u4fd2","\u4fd3","\u4fd4","\u4fd5","\u4fd6","\u4fd7","\u4fd8","\u4fd9","\u4fda","\u4fdb","\u4fdc","\u4fde","\u4fdd","\u4fdf","\u4fe0","\u4fe1","\u4fe2","\u4fe3","\u4fe4","\u4fe5","\u4fe6","\u4fe7","\u4fe8","\u4fe9","\u4fea","\u4feb","\u4fec","\u4fee","\u4fed","\u4fef","\u4ff0","\u4ff1","\u4ff2","\u4ff3","\u4ff4","\u4ff5","\u4ff6","\u4ff7","\u4ff8","\u4ff9","\u4ffa","\u4ffb","\u4ffc","\u4ffe","\u4ffd","\u4fff",
										"\u5000","\u5001","\u5002","\u5003","\u5004","\u5005","\u5006","\u5007","\u5008","\u5009","\u500a","\u500b","\u500c","\u500e","\u500d","\u500f","\u5010","\u5011","\u5012","\u5013","\u5014","\u5015","\u5016","\u5017","\u5018","\u5019","\u501a","\u501b","\u501c","\u501e","\u501d","\u501f","\u5020","\u5021","\u5022","\u5023","\u5024","\u5025","\u5026","\u5027","\u5028","\u5029","\u502a","\u502b","\u502c","\u502e","\u502d","\u502f","\u5030","\u5031","\u5032","\u5033","\u5034","\u5035","\u5036","\u5037","\u5038","\u5039","\u503a","\u503b","\u503c","\u503e","\u503d","\u503f","\u5040","\u5041","\u5042","\u5043","\u5044","\u5045","\u5046","\u5047","\u5048","\u5049","\u504a","\u504b","\u504c","\u504e","\u504d","\u504f","\u5050","\u5051","\u5052","\u5053","\u5054","\u5055","\u5056","\u5057","\u5058","\u5059","\u505a","\u505b","\u505c","\u505e","\u505d","\u505f","\u5060","\u5061","\u5062","\u5063","\u5064","\u5065","\u5066","\u5067","\u5068","\u5069","\u506a","\u506b","\u506c","\u506e","\u506d","\u506f","\u5070","\u5071","\u5072","\u5073","\u5074","\u5075","\u5076","\u5077","\u5078","\u5079","\u507a","\u507b","\u507c","\u507e","\u507d","\u507f","\u5080","\u5081","\u5082","\u5083","\u5084","\u5085","\u5086","\u5087","\u5088","\u5089","\u508a","\u508b","\u508c","\u508e","\u508d","\u508f","\u5090","\u5091","\u5092","\u5093","\u5094","\u5095","\u5096","\u5097","\u5098","\u5099","\u509a","\u509b","\u509c","\u509e","\u509d","\u509f","\u50a0","\u50a1","\u50a2","\u50a3","\u50a4","\u50a5","\u50a6","\u50a7","\u50a8","\u50a9","\u50aa","\u50ab","\u50ac","\u50ae","\u50ad","\u50af","\u50b0","\u50b1","\u50b2","\u50b3","\u50b4","\u50b5","\u50b6","\u50b7","\u50b8","\u50b9","\u50ba","\u50bb","\u50bc","\u50be","\u50bd","\u50bf","\u50c0","\u50c1","\u50c2","\u50c3","\u50c4","\u50c5","\u50c6","\u50c7","\u50c8","\u50c9","\u50ca","\u50cb","\u50cc","\u50ce","\u50cd","\u50cf","\u50d0","\u50d1","\u50d2","\u50d3","\u50d4","\u50d5","\u50d6","\u50d7","\u50d8","\u50d9","\u50da","\u50db","\u50dc","\u50de","\u50dd","\u50df","\u50e0","\u50e1","\u50e2","\u50e3","\u50e4","\u50e5","\u50e6","\u50e7","\u50e8","\u50e9","\u50ea","\u50eb","\u50ec","\u50ee","\u50ed","\u50ef","\u50f0","\u50f1","\u50f2","\u50f3","\u50f4","\u50f5","\u50f6","\u50f7","\u50f8","\u50f9","\u50fa","\u50fb","\u50fc","\u50fe","\u50fd","\u50ff",
										"\u9c00","\u9c01","\u9c02","\u9c03","\u9c04","\u9c05","\u9c06","\u9c07","\u9c08","\u9c09","\u9c0a","\u9c0b","\u9c0c","\u9c0e","\u9c0d","\u9c0f","\u9c10","\u9c11","\u9c12","\u9c13","\u9c14","\u9c15","\u9c16","\u9c17","\u9c18","\u9c19","\u9c1a","\u9c1b","\u9c1c","\u9c1e","\u9c1d","\u9c1f","\u9c20","\u9c21","\u9c22","\u9c23","\u9c24","\u9c25","\u9c26","\u9c27","\u9c28","\u9c29","\u9c2a","\u9c2b","\u9c2c","\u9c2e","\u9c2d","\u9c2f","\u9c30","\u9c31","\u9c32","\u9c33","\u9c34","\u9c35","\u9c36","\u9c37","\u9c38","\u9c39","\u9c3a","\u9c3b","\u9c3c","\u9c3e","\u9c3d","\u9c3f","\u9c40","\u9c41","\u9c42","\u9c43","\u9c44","\u9c45","\u9c46","\u9c47","\u9c48","\u9c49","\u9c4a","\u9c4b","\u9c4c","\u9c4e","\u9c4d","\u9c4f","\u9c50","\u9c51","\u9c52","\u9c53","\u9c54","\u9c55","\u9c56","\u9c57","\u9c58","\u9c59","\u9c5a","\u9c5b","\u9c5c","\u9c5e","\u9c5d","\u9c5f","\u9c60","\u9c61","\u9c62","\u9c63","\u9c64","\u9c65","\u9c66","\u9c67","\u9c68","\u9c69","\u9c6a","\u9c6b","\u9c6c","\u9c6e","\u9c6d","\u9c6f","\u9c70","\u9c71","\u9c72","\u9c73","\u9c74","\u9c75","\u9c76","\u9c77","\u9c78","\u9c79","\u9c7a","\u9c7b","\u9c7c","\u9c7e","\u9c7d","\u9c7f","\u9c80","\u9c81","\u9c82","\u9c83","\u9c84","\u9c85","\u9c86","\u9c87","\u9c88","\u9c89","\u9c8a","\u9c8b","\u9c8c","\u9c8e","\u9c8d","\u9c8f","\u9c90","\u9c91","\u9c92","\u9c93","\u9c94","\u9c95","\u9c96","\u9c97","\u9c98","\u9c99","\u9c9a","\u9c9b","\u9c9c","\u9c9e","\u9c9d","\u9c9f","\u9ca0","\u9ca1","\u9ca2","\u9ca3","\u9ca4","\u9ca5","\u9ca6","\u9ca7","\u9ca8","\u9ca9","\u9caa","\u9cab","\u9cac","\u9cae","\u9cad","\u9caf","\u9cb0","\u9cb1","\u9cb2","\u9cb3","\u9cb4","\u9cb5","\u9cb6","\u9cb7","\u9cb8","\u9cb9","\u9cba","\u9cbb","\u9cbc","\u9cbe","\u9cbd","\u9cbf","\u9cc0","\u9cc1","\u9cc2","\u9cc3","\u9cc4","\u9cc5","\u9cc6","\u9cc7","\u9cc8","\u9cc9","\u9cca","\u9ccb","\u9ccc","\u9cce","\u9ccd","\u9ccf","\u9cd0","\u9cd1","\u9cd2","\u9cd3","\u9cd4","\u9cd5","\u9cd6","\u9cd7","\u9cd8","\u9cd9","\u9cda","\u9cdb","\u9cdc","\u9cde","\u9cdd","\u9cdf","\u9ce0","\u9ce1","\u9ce2","\u9ce3","\u9ce4","\u9ce5","\u9ce6","\u9ce7","\u9ce8","\u9ce9","\u9cea","\u9ceb","\u9cec","\u9cee","\u9ced","\u9cef","\u9cf0","\u9cf1","\u9cf2","\u9cf3","\u9cf4","\u9cf5","\u9cf6","\u9cf7","\u9cf8","\u9cf9","\u9cfa","\u9cfb","\u9cfc","\u9cfe","\u9cfd","\u9cff",
										"\u9d00","\u9d01","\u9d02","\u9d03","\u9d04","\u9d05","\u9d06","\u9d07","\u9d08","\u9d09","\u9d0a","\u9d0b","\u9d0c","\u9d0e","\u9d0d","\u9d0f","\u9d10","\u9d11","\u9d12","\u9d13","\u9d14","\u9d15","\u9d16","\u9d17","\u9d18","\u9d19","\u9d1a","\u9d1b","\u9d1c","\u9d1e","\u9d1d","\u9d1f","\u9d20","\u9d21","\u9d22","\u9d23","\u9d24","\u9d25","\u9d26","\u9d27","\u9d28","\u9d29","\u9d2a","\u9d2b","\u9d2c","\u9d2e","\u9d2d","\u9d2f","\u9d30","\u9d31","\u9d32","\u9d33","\u9d34","\u9d35","\u9d36","\u9d37","\u9d38","\u9d39","\u9d3a","\u9d3b","\u9d3c","\u9d3e","\u9d3d","\u9d3f","\u9d40","\u9d41","\u9d42","\u9d43","\u9d44","\u9d45","\u9d46","\u9d47","\u9d48","\u9d49","\u9d4a","\u9d4b","\u9d4c","\u9d4e","\u9d4d","\u9d4f","\u9d50","\u9d51","\u9d52","\u9d53","\u9d54","\u9d55","\u9d56","\u9d57","\u9d58","\u9d59","\u9d5a","\u9d5b","\u9d5c","\u9d5e","\u9d5d","\u9d5f","\u9d60","\u9d61","\u9d62","\u9d63","\u9d64","\u9d65","\u9d66","\u9d67","\u9d68","\u9d69","\u9d6a","\u9d6b","\u9d6c","\u9d6e","\u9d6d","\u9d6f","\u9d70","\u9d71","\u9d72","\u9d73","\u9d74","\u9d75","\u9d76","\u9d77","\u9d78","\u9d79","\u9d7a","\u9d7b","\u9d7c","\u9d7e","\u9d7d","\u9d7f","\u9d80","\u9d81","\u9d82","\u9d83","\u9d84","\u9d85","\u9d86","\u9d87","\u9d88","\u9d89","\u9d8a","\u9d8b","\u9d8c","\u9d8e","\u9d8d","\u9d8f","\u9d90","\u9d91","\u9d92","\u9d93","\u9d94","\u9d95","\u9d96","\u9d97","\u9d98","\u9d99","\u9d9a","\u9d9b","\u9d9c","\u9d9e","\u9d9d","\u9d9f","\u9da0","\u9da1","\u9da2","\u9da3","\u9da4","\u9da5","\u9da6","\u9da7","\u9da8","\u9da9","\u9daa","\u9dab","\u9dac","\u9dae","\u9dad","\u9daf","\u9db0","\u9db1","\u9db2","\u9db3","\u9db4","\u9db5","\u9db6","\u9db7","\u9db8","\u9db9","\u9dba","\u9dbb","\u9dbc","\u9dbe","\u9dbd","\u9dbf","\u9dc0","\u9dc1","\u9dc2","\u9dc3","\u9dc4","\u9dc5","\u9dc6","\u9dc7","\u9dc8","\u9dc9","\u9dca","\u9dcb","\u9dcc","\u9dce","\u9dcd","\u9dcf","\u9dd0","\u9dd1","\u9dd2","\u9dd3","\u9dd4","\u9dd5","\u9dd6","\u9dd7","\u9dd8","\u9dd9","\u9dda","\u9ddb","\u9ddc","\u9dde","\u9ddd","\u9ddf","\u9de0","\u9de1","\u9de2","\u9de3","\u9de4","\u9de5","\u9de6","\u9de7","\u9de8","\u9de9","\u9dea","\u9deb","\u9dec","\u9dee","\u9ded","\u9def","\u9df0","\u9df1","\u9df2","\u9df3","\u9df4","\u9df5","\u9df6","\u9df7","\u9df8","\u9df9","\u9dfa","\u9dfb","\u9dfc","\u9dfe","\u9dfd","\u9dff",
										"\uac00","\uac01","\uac02","\uac03","\uac04","\uac05","\uac06","\uac07","\uac08","\uac09","\uac0a","\uac0b","\uac0c","\uac0e","\uac0d","\uac0f","\uac10","\uac11","\uac12","\uac13","\uac14","\uac15","\uac16","\uac17","\uac18","\uac19","\uac1a","\uac1b","\uac1c","\uac1e","\uac1d","\uac1f","\uac20","\uac21","\uac22","\uac23","\uac24","\uac25","\uac26","\uac27","\uac28","\uac29","\uac2a","\uac2b","\uac2c","\uac2e","\uac2d","\uac2f","\uac30","\uac31","\uac32","\uac33","\uac34","\uac35","\uac36","\uac37","\uac38","\uac39","\uac3a","\uac3b","\uac3c","\uac3e","\uac3d","\uac3f","\uac40","\uac41","\uac42","\uac43","\uac44","\uac45","\uac46","\uac47","\uac48","\uac49","\uac4a","\uac4b","\uac4c","\uac4e","\uac4d","\uac4f","\uac50","\uac51","\uac52","\uac53","\uac54","\uac55","\uac56","\uac57","\uac58","\uac59","\uac5a","\uac5b","\uac5c","\uac5e","\uac5d","\uac5f","\uac60","\uac61","\uac62","\uac63","\uac64","\uac65","\uac66","\uac67","\uac68","\uac69","\uac6a","\uac6b","\uac6c","\uac6e","\uac6d","\uac6f","\uac70","\uac71","\uac72","\uac73","\uac74","\uac75","\uac76","\uac77","\uac78","\uac79","\uac7a","\uac7b","\uac7c","\uac7e","\uac7d","\uac7f","\uac80","\uac81","\uac82","\uac83","\uac84","\uac85","\uac86","\uac87","\uac88","\uac89","\uac8a","\uac8b","\uac8c","\uac8e","\uac8d","\uac8f","\uac90","\uac91","\uac92","\uac93","\uac94","\uac95","\uac96","\uac97","\uac98","\uac99","\uac9a","\uac9b","\uac9c","\uac9e","\uac9d","\uac9f","\uaca0","\uaca1","\uaca2","\uaca3","\uaca4","\uaca5","\uaca6","\uaca7","\uaca8","\uaca9","\uacaa","\uacab","\uacac","\uacae","\uacad","\uacaf","\uacb0","\uacb1","\uacb2","\uacb3","\uacb4","\uacb5","\uacb6","\uacb7","\uacb8","\uacb9","\uacba","\uacbb","\uacbc","\uacbe","\uacbd","\uacbf","\uacc0","\uacc1","\uacc2","\uacc3","\uacc4","\uacc5","\uacc6","\uacc7","\uacc8","\uacc9","\uacca","\uaccb","\uaccc","\uacce","\uaccd","\uaccf","\uacd0","\uacd1","\uacd2","\uacd3","\uacd4","\uacd5","\uacd6","\uacd7","\uacd8","\uacd9","\uacda","\uacdb","\uacdc","\uacde","\uacdd","\uacdf","\uace0","\uace1","\uace2","\uace3","\uace4","\uace5","\uace6","\uace7","\uace8","\uace9","\uacea","\uaceb","\uacec","\uacee","\uaced","\uacef","\uacf0","\uacf1","\uacf2","\uacf3","\uacf4","\uacf5","\uacf6","\uacf7","\uacf8","\uacf9","\uacfa","\uacfb","\uacfc","\uacfe","\uacfd","\uacff",
										"\uad00","\uad01","\uad02","\uad03","\uad04","\uad05","\uad06","\uad07","\uad08","\uad09","\uad0a","\uad0b","\uad0c","\uad0e","\uad0d","\uad0f","\uad10","\uad11","\uad12","\uad13","\uad14","\uad15","\uad16","\uad17","\uad18","\uad19","\uad1a","\uad1b","\uad1c","\uad1e","\uad1d","\uad1f","\uad20","\uad21","\uad22","\uad23","\uad24","\uad25","\uad26","\uad27","\uad28","\uad29","\uad2a","\uad2b","\uad2c","\uad2e","\uad2d","\uad2f","\uad30","\uad31","\uad32","\uad33","\uad34","\uad35","\uad36","\uad37","\uad38","\uad39","\uad3a","\uad3b","\uad3c","\uad3e","\uad3d","\uad3f","\uad40","\uad41","\uad42","\uad43","\uad44","\uad45","\uad46","\uad47","\uad48","\uad49","\uad4a","\uad4b","\uad4c","\uad4e","\uad4d","\uad4f","\uad50","\uad51","\uad52","\uad53","\uad54","\uad55","\uad56","\uad57","\uad58","\uad59","\uad5a","\uad5b","\uad5c","\uad5e","\uad5d","\uad5f","\uad60","\uad61","\uad62","\uad63","\uad64","\uad65","\uad66","\uad67","\uad68","\uad69","\uad6a","\uad6b","\uad6c","\uad6e","\uad6d","\uad6f","\uad70","\uad71","\uad72","\uad73","\uad74","\uad75","\uad76","\uad77","\uad78","\uad79","\uad7a","\uad7b","\uad7c","\uad7e","\uad7d","\uad7f","\uad80","\uad81","\uad82","\uad83","\uad84","\uad85","\uad86","\uad87","\uad88","\uad89","\uad8a","\uad8b","\uad8c","\uad8e","\uad8d","\uad8f","\uad90","\uad91","\uad92","\uad93","\uad94","\uad95","\uad96","\uad97","\uad98","\uad99","\uad9a","\uad9b","\uad9c","\uad9e","\uad9d","\uad9f","\uada0","\uada1","\uada2","\uada3","\uada4","\uada5","\uada6","\uada7","\uada8","\uada9","\uadaa","\uadab","\uadac","\uadae","\uadad","\uadaf","\uadb0","\uadb1","\uadb2","\uadb3","\uadb4","\uadb5","\uadb6","\uadb7","\uadb8","\uadb9","\uadba","\uadbb","\uadbc","\uadbe","\uadbd","\uadbf","\uadc0","\uadc1","\uadc2","\uadc3","\uadc4","\uadc5","\uadc6","\uadc7","\uadc8","\uadc9","\uadca","\uadcb","\uadcc","\uadce","\uadcd","\uadcf","\uadd0","\uadd1","\uadd2","\uadd3","\uadd4","\uadd5","\uadd6","\uadd7","\uadd8","\uadd9","\uadda","\uaddb","\uaddc","\uadde","\uaddd","\uaddf","\uade0","\uade1","\uade2","\uade3","\uade4","\uade5","\uade6","\uade7","\uade8","\uade9","\uadea","\uadeb","\uadec","\uadee","\uaded","\uadef","\uadf0","\uadf1","\uadf2","\uadf3","\uadf4","\uadf5","\uadf6","\uadf7","\uadf8","\uadf9","\uadfa","\uadfb","\uadfc","\uadfe","\uadfd","\uadff",
										"\uae00","\uae01","\uae02","\uae03","\uae04","\uae05","\uae06","\uae07","\uae08","\uae09","\uae0a","\uae0b","\uae0c","\uae0e","\uae0d","\uae0f","\uae10","\uae11","\uae12","\uae13","\uae14","\uae15","\uae16","\uae17","\uae18","\uae19","\uae1a","\uae1b","\uae1c","\uae1e","\uae1d","\uae1f","\uae20","\uae21","\uae22","\uae23","\uae24","\uae25","\uae26","\uae27","\uae28","\uae29","\uae2a","\uae2b","\uae2c","\uae2e","\uae2d","\uae2f","\uae30","\uae31","\uae32","\uae33","\uae34","\uae35","\uae36","\uae37","\uae38","\uae39","\uae3a","\uae3b","\uae3c","\uae3e","\uae3d","\uae3f","\uae40","\uae41","\uae42","\uae43","\uae44","\uae45","\uae46","\uae47","\uae48","\uae49","\uae4a","\uae4b","\uae4c","\uae4e","\uae4d","\uae4f","\uae50","\uae51","\uae52","\uae53","\uae54","\uae55","\uae56","\uae57","\uae58","\uae59","\uae5a","\uae5b","\uae5c","\uae5e","\uae5d","\uae5f","\uae60","\uae61","\uae62","\uae63","\uae64","\uae65","\uae66","\uae67","\uae68","\uae69","\uae6a","\uae6b","\uae6c","\uae6e","\uae6d","\uae6f","\uae70","\uae71","\uae72","\uae73","\uae74","\uae75","\uae76","\uae77","\uae78","\uae79","\uae7a","\uae7b","\uae7c","\uae7e","\uae7d","\uae7f","\uae80","\uae81","\uae82","\uae83","\uae84","\uae85","\uae86","\uae87","\uae88","\uae89","\uae8a","\uae8b","\uae8c","\uae8e","\uae8d","\uae8f","\uae90","\uae91","\uae92","\uae93","\uae94","\uae95","\uae96","\uae97","\uae98","\uae99","\uae9a","\uae9b","\uae9c","\uae9e","\uae9d","\uae9f","\uaea0","\uaea1","\uaea2","\uaea3","\uaea4","\uaea5","\uaea6","\uaea7","\uaea8","\uaea9","\uaeaa","\uaeab","\uaeac","\uaeae","\uaead","\uaeaf","\uaeb0","\uaeb1","\uaeb2","\uaeb3","\uaeb4","\uaeb5","\uaeb6","\uaeb7","\uaeb8","\uaeb9","\uaeba","\uaebb","\uaebc","\uaebe","\uaebd","\uaebf","\uaec0","\uaec1","\uaec2","\uaec3","\uaec4","\uaec5","\uaec6","\uaec7","\uaec8","\uaec9","\uaeca","\uaecb","\uaecc","\uaece","\uaecd","\uaecf","\uaed0","\uaed1","\uaed2","\uaed3","\uaed4","\uaed5","\uaed6","\uaed7","\uaed8","\uaed9","\uaeda","\uaedb","\uaedc","\uaede","\uaedd","\uaedf","\uaee0","\uaee1","\uaee2","\uaee3","\uaee4","\uaee5","\uaee6","\uaee7","\uaee8","\uaee9","\uaeea","\uaeeb","\uaeec","\uaeee","\uaeed","\uaeef","\uaef0","\uaef1","\uaef2","\uaef3","\uaef4","\uaef5","\uaef6","\uaef7","\uaef8","\uaef9","\uaefa","\uaefb","\uaefc","\uaefe","\uaefd","\uaeff",
										"\uaf00","\uaf01","\uaf02","\uaf03","\uaf04","\uaf05","\uaf06","\uaf07","\uaf08","\uaf09","\uaf0a","\uaf0b","\uaf0c","\uaf0e","\uaf0d","\uaf0f","\uaf10","\uaf11","\uaf12","\uaf13","\uaf14","\uaf15","\uaf16","\uaf17","\uaf18","\uaf19","\uaf1a","\uaf1b","\uaf1c","\uaf1e","\uaf1d","\uaf1f","\uaf20","\uaf21","\uaf22","\uaf23","\uaf24","\uaf25","\uaf26","\uaf27","\uaf28","\uaf29","\uaf2a","\uaf2b","\uaf2c","\uaf2e","\uaf2d","\uaf2f","\uaf30","\uaf31","\uaf32","\uaf33","\uaf34","\uaf35","\uaf36","\uaf37","\uaf38","\uaf39","\uaf3a","\uaf3b","\uaf3c","\uaf3e","\uaf3d","\uaf3f","\uaf40","\uaf41","\uaf42","\uaf43","\uaf44","\uaf45","\uaf46","\uaf47","\uaf48","\uaf49","\uaf4a","\uaf4b","\uaf4c","\uaf4e","\uaf4d","\uaf4f","\uaf50","\uaf51","\uaf52","\uaf53","\uaf54","\uaf55","\uaf56","\uaf57","\uaf58","\uaf59","\uaf5a","\uaf5b","\uaf5c","\uaf5e","\uaf5d","\uaf5f","\uaf60","\uaf61","\uaf62","\uaf63","\uaf64","\uaf65","\uaf66","\uaf67","\uaf68","\uaf69","\uaf6a","\uaf6b","\uaf6c","\uaf6e","\uaf6d","\uaf6f","\uaf70","\uaf71","\uaf72","\uaf73","\uaf74","\uaf75","\uaf76","\uaf77","\uaf78","\uaf79","\uaf7a","\uaf7b","\uaf7c","\uaf7e","\uaf7d","\uaf7f","\uaf80","\uaf81","\uaf82","\uaf83","\uaf84","\uaf85","\uaf86","\uaf87","\uaf88","\uaf89","\uaf8a","\uaf8b","\uaf8c","\uaf8e","\uaf8d","\uaf8f","\uaf90","\uaf91","\uaf92","\uaf93","\uaf94","\uaf95","\uaf96","\uaf97","\uaf98","\uaf99","\uaf9a","\uaf9b","\uaf9c","\uaf9e","\uaf9d","\uaf9f","\uafa0","\uafa1","\uafa2","\uafa3","\uafa4","\uafa5","\uafa6","\uafa7","\uafa8","\uafa9","\uafaa","\uafab","\uafac","\uafae","\uafad","\uafaf","\uafb0","\uafb1","\uafb2","\uafb3","\uafb4","\uafb5","\uafb6","\uafb7","\uafb8","\uafb9","\uafba","\uafbb","\uafbc","\uafbe","\uafbd","\uafbf","\uafc0","\uafc1","\uafc2","\uafc3","\uafc4","\uafc5","\uafc6","\uafc7","\uafc8","\uafc9","\uafca","\uafcb","\uafcc","\uafce","\uafcd","\uafcf","\uafd0","\uafd1","\uafd2","\uafd3","\uafd4","\uafd5","\uafd6","\uafd7","\uafd8","\uafd9","\uafda","\uafdb","\uafdc","\uafde","\uafdd","\uafdf","\uafe0","\uafe1","\uafe2","\uafe3","\uafe4","\uafe5","\uafe6","\uafe7","\uafe8","\uafe9","\uafea","\uafeb","\uafec","\uafee","\uafed","\uafef","\uaff0","\uaff1","\uaff2","\uaff3","\uaff4","\uaff5","\uaff6","\uaff7","\uaff8","\uaff9","\uaffa","\uaffb","\uaffc","\uaffe","\uaffd","\uafff",
										"\ub000","\ub001","\ub002","\ub003","\ub004","\ub005","\ub006","\ub007","\ub008","\ub009","\ub00a","\ub00b","\ub00c","\ub00e","\ub00d","\ub00f","\ub010","\ub011","\ub012","\ub013","\ub014","\ub015","\ub016","\ub017","\ub018","\ub019","\ub01a","\ub01b","\ub01c","\ub01e","\ub01d","\ub01f","\ub020","\ub021","\ub022","\ub023","\ub024","\ub025","\ub026","\ub027","\ub028","\ub029","\ub02a","\ub02b","\ub02c","\ub02e","\ub02d","\ub02f","\ub030","\ub031","\ub032","\ub033","\ub034","\ub035","\ub036","\ub037","\ub038","\ub039","\ub03a","\ub03b","\ub03c","\ub03e","\ub03d","\ub03f","\ub040","\ub041","\ub042","\ub043","\ub044","\ub045","\ub046","\ub047","\ub048","\ub049","\ub04a","\ub04b","\ub04c","\ub04e","\ub04d","\ub04f","\ub050","\ub051","\ub052","\ub053","\ub054","\ub055","\ub056","\ub057","\ub058","\ub059","\ub05a","\ub05b","\ub05c","\ub05e","\ub05d","\ub05f","\ub060","\ub061","\ub062","\ub063","\ub064","\ub065","\ub066","\ub067","\ub068","\ub069","\ub06a","\ub06b","\ub06c","\ub06e","\ub06d","\ub06f","\ub070","\ub071","\ub072","\ub073","\ub074","\ub075","\ub076","\ub077","\ub078","\ub079","\ub07a","\ub07b","\ub07c","\ub07e","\ub07d","\ub07f","\ub080","\ub081","\ub082","\ub083","\ub084","\ub085","\ub086","\ub087","\ub088","\ub089","\ub08a","\ub08b","\ub08c","\ub08e","\ub08d","\ub08f","\ub090","\ub091","\ub092","\ub093","\ub094","\ub095","\ub096","\ub097","\ub098","\ub099","\ub09a","\ub09b","\ub09c","\ub09e","\ub09d","\ub09f","\ub0a0","\ub0a1","\ub0a2","\ub0a3","\ub0a4","\ub0a5","\ub0a6","\ub0a7","\ub0a8","\ub0a9","\ub0aa","\ub0ab","\ub0ac","\ub0ae","\ub0ad","\ub0af","\ub0b0","\ub0b1","\ub0b2","\ub0b3","\ub0b4","\ub0b5","\ub0b6","\ub0b7","\ub0b8","\ub0b9","\ub0ba","\ub0bb","\ub0bc","\ub0be","\ub0bd","\ub0bf","\ub0c0","\ub0c1","\ub0c2","\ub0c3","\ub0c4","\ub0c5","\ub0c6","\ub0c7","\ub0c8","\ub0c9","\ub0ca","\ub0cb","\ub0cc","\ub0ce","\ub0cd","\ub0cf","\ub0d0","\ub0d1","\ub0d2","\ub0d3","\ub0d4","\ub0d5","\ub0d6","\ub0d7","\ub0d8","\ub0d9","\ub0da","\ub0db","\ub0dc","\ub0de","\ub0dd","\ub0df","\ub0e0","\ub0e1","\ub0e2","\ub0e3","\ub0e4","\ub0e5","\ub0e6","\ub0e7","\ub0e8","\ub0e9","\ub0ea","\ub0eb","\ub0ec","\ub0ee","\ub0ed","\ub0ef","\ub0f0","\ub0f1","\ub0f2","\ub0f3","\ub0f4","\ub0f5","\ub0f6","\ub0f7","\ub0f8","\ub0f9","\ub0fa","\ub0fb","\ub0fc","\ub0fe","\ub0fd","\ub0ff",
										"\ub100","\ub101","\ub102","\ub103","\ub104","\ub105","\ub106","\ub107","\ub108","\ub109","\ub10a","\ub10b","\ub10c","\ub10e","\ub10d","\ub10f","\ub110","\ub111","\ub112","\ub113","\ub114","\ub115","\ub116","\ub117","\ub118","\ub119","\ub11a","\ub11b","\ub11c","\ub11e","\ub11d","\ub11f","\ub120","\ub121","\ub122","\ub123","\ub124","\ub125","\ub126","\ub127","\ub128","\ub129","\ub12a","\ub12b","\ub12c","\ub12e","\ub12d","\ub12f","\ub130","\ub131","\ub132","\ub133","\ub134","\ub135","\ub136","\ub137","\ub138","\ub139","\ub13a","\ub13b","\ub13c","\ub13e","\ub13d","\ub13f","\ub140","\ub141","\ub142","\ub143","\ub144","\ub145","\ub146","\ub147","\ub148","\ub149","\ub14a","\ub14b","\ub14c","\ub14e","\ub14d","\ub14f","\ub150","\ub151","\ub152","\ub153","\ub154","\ub155","\ub156","\ub157","\ub158","\ub159","\ub15a","\ub15b","\ub15c","\ub15e","\ub15d","\ub15f","\ub160","\ub161","\ub162","\ub163","\ub164","\ub165","\ub166","\ub167","\ub168","\ub169","\ub16a","\ub16b","\ub16c","\ub16e","\ub16d","\ub16f","\ub170","\ub171","\ub172","\ub173","\ub174","\ub175","\ub176","\ub177","\ub178","\ub179","\ub17a","\ub17b","\ub17c","\ub17e","\ub17d","\ub17f","\ub180","\ub181","\ub182","\ub183","\ub184","\ub185","\ub186","\ub187","\ub188","\ub189","\ub18a","\ub18b","\ub18c","\ub18e","\ub18d","\ub18f","\ub190","\ub191","\ub192","\ub193","\ub194","\ub195","\ub196","\ub197","\ub198","\ub199","\ub19a","\ub19b","\ub19c","\ub19e","\ub19d","\ub19f","\ub1a0","\ub1a1","\ub1a2","\ub1a3","\ub1a4","\ub1a5","\ub1a6","\ub1a7","\ub1a8","\ub1a9","\ub1aa","\ub1ab","\ub1ac","\ub1ae","\ub1ad","\ub1af","\ub1b0","\ub1b1","\ub1b2","\ub1b3","\ub1b4","\ub1b5","\ub1b6","\ub1b7","\ub1b8","\ub1b9","\ub1ba","\ub1bb","\ub1bc","\ub1be","\ub1bd","\ub1bf","\ub1c0","\ub1c1","\ub1c2","\ub1c3","\ub1c4","\ub1c5","\ub1c6","\ub1c7","\ub1c8","\ub1c9","\ub1ca","\ub1cb","\ub1cc","\ub1ce","\ub1cd","\ub1cf","\ub1d0","\ub1d1","\ub1d2","\ub1d3","\ub1d4","\ub1d5","\ub1d6","\ub1d7","\ub1d8","\ub1d9","\ub1da","\ub1db","\ub1dc","\ub1de","\ub1dd","\ub1df","\ub1e0","\ub1e1","\ub1e2","\ub1e3","\ub1e4","\ub1e5","\ub1e6","\ub1e7","\ub1e8","\ub1e9","\ub1ea","\ub1eb","\ub1ec","\ub1ee","\ub1ed","\ub1ef","\ub1f0","\ub1f1","\ub1f2","\ub1f3","\ub1f4","\ub1f5","\ub1f6","\ub1f7","\ub1f8","\ub1f9","\ub1fa","\ub1fb","\ub1fc","\ub1fe","\ub1fd","\ub1ff",
										"\uf900","\uf901","\uf902","\uf903","\uf904","\uf905","\uf906","\uf907","\uf908","\uf909","\uf90a","\uf90b","\uf90c","\uf90e","\uf90d","\uf90f","\uf910","\uf911","\uf912","\uf913","\uf914","\uf915","\uf916","\uf917","\uf918","\uf919","\uf91a","\uf91b","\uf91c","\uf91e","\uf91d","\uf91f","\uf920","\uf921","\uf922","\uf923","\uf924","\uf925","\uf926","\uf927","\uf928","\uf929","\uf92a","\uf92b","\uf92c","\uf92e","\uf92d","\uf92f","\uf930","\uf931","\uf932","\uf933","\uf934","\uf935","\uf936","\uf937","\uf938","\uf939","\uf93a","\uf93b","\uf93c","\uf93e","\uf93d","\uf93f","\uf940","\uf941","\uf942","\uf943","\uf944","\uf945","\uf946","\uf947","\uf948","\uf949","\uf94a","\uf94b","\uf94c","\uf94e","\uf94d","\uf94f","\uf950","\uf951","\uf952","\uf953","\uf954","\uf955","\uf956","\uf957","\uf958","\uf959","\uf95a","\uf95b","\uf95c","\uf95e","\uf95d","\uf95f","\uf960","\uf961","\uf962","\uf963","\uf964","\uf965","\uf966","\uf967","\uf968","\uf969","\uf96a","\uf96b","\uf96c","\uf96e","\uf96d","\uf96f","\uf970","\uf971","\uf972","\uf973","\uf974","\uf975","\uf976","\uf977","\uf978","\uf979","\uf97a","\uf97b","\uf97c","\uf97e","\uf97d","\uf97f","\uf980","\uf981","\uf982","\uf983","\uf984","\uf985","\uf986","\uf987","\uf988","\uf989","\uf98a","\uf98b","\uf98c","\uf98e","\uf98d","\uf98f","\uf990","\uf991","\uf992","\uf993","\uf994","\uf995","\uf996","\uf997","\uf998","\uf999","\uf99a","\uf99b","\uf99c","\uf99e","\uf99d","\uf99f","\uf9a0","\uf9a1","\uf9a2","\uf9a3","\uf9a4","\uf9a5","\uf9a6","\uf9a7","\uf9a8","\uf9a9","\uf9aa","\uf9ab","\uf9ac","\uf9ae","\uf9ad","\uf9af","\uf9b0","\uf9b1","\uf9b2","\uf9b3","\uf9b4","\uf9b5","\uf9b6","\uf9b7","\uf9b8","\uf9b9","\uf9ba","\uf9bb","\uf9bc","\uf9be","\uf9bd","\uf9bf","\uf9c0","\uf9c1","\uf9c2","\uf9c3","\uf9c4","\uf9c5","\uf9c6","\uf9c7","\uf9c8","\uf9c9","\uf9ca","\uf9cb","\uf9cc","\uf9ce","\uf9cd","\uf9cf","\uf9d0","\uf9d1","\uf9d2","\uf9d3","\uf9d4","\uf9d5","\uf9d6","\uf9d7","\uf9d8","\uf9d9","\uf9da","\uf9db","\uf9dc","\uf9de","\uf9dd","\uf9df","\uf9e0","\uf9e1","\uf9e2","\uf9e3","\uf9e4","\uf9e5","\uf9e6","\uf9e7","\uf9e8","\uf9e9","\uf9ea","\uf9eb","\uf9ec","\uf9ee","\uf9ed","\uf9ef","\uf9f0","\uf9f1","\uf9f2","\uf9f3","\uf9f4","\uf9f5","\uf9f6","\uf9f7","\uf9f8","\uf9f9","\uf9fa","\uf9fb","\uf9fc","\uf9fe","\uf9fd","\uf9ff",
										"\uff00","\uff01","\uff02","\uff03","\uff04","\uff05","\uff06","\uff07","\uff08","\uff09","\uff0a","\uff0b","\uff0c","\uff0e","\uff0d","\uff0f","\uff10","\uff11","\uff12","\uff13","\uff14","\uff15","\uff16","\uff17","\uff18","\uff19","\uff1a","\uff1b","\uff1c","\uff1e","\uff1d","\uff1f","\uff20","\uff21","\uff22","\uff23","\uff24","\uff25","\uff26","\uff27","\uff28","\uff29","\uff2a","\uff2b","\uff2c","\uff2e","\uff2d","\uff2f","\uff30","\uff31","\uff32","\uff33","\uff34","\uff35","\uff36","\uff37","\uff38","\uff39","\uff3a","\uff3b","\uff3c","\uff3e","\uff3d","\uff3f","\uff40","\uff41","\uff42","\uff43","\uff44","\uff45","\uff46","\uff47","\uff48","\uff49","\uff4a","\uff4b","\uff4c","\uff4e","\uff4d","\uff4f","\uff50","\uff51","\uff52","\uff53","\uff54","\uff55","\uff56","\uff57","\uff58","\uff59","\uff5a","\uff5b","\uff5c","\uff5e","\uff5d","\uff5f","\uff60","\uff61","\uff62","\uff63","\uff64","\uff65","\uff66","\uff67","\uff68","\uff69","\uff6a","\uff6b","\uff6c","\uff6e","\uff6d","\uff6f","\uff70","\uff71","\uff72","\uff73","\uff74","\uff75","\uff76","\uff77","\uff78","\uff79","\uff7a","\uff7b","\uff7c","\uff7e","\uff7d","\uff7f","\uff80","\uff81","\uff82","\uff83","\uff84","\uff85","\uff86","\uff87","\uff88","\uff89","\uff8a","\uff8b","\uff8c","\uff8e","\uff8d","\uff8f","\uff90","\uff91","\uff92","\uff93","\uff94","\uff95","\uff96","\uff97","\uff98","\uff99","\uff9a","\uff9b","\uff9c","\uff9e","\uff9d","\uff9f","\uffa0","\uffa1","\uffa2","\uffa3","\uffa4","\uffa5","\uffa6","\uffa7","\uffa8","\uffa9","\uffaa","\uffab","\uffac","\uffae","\uffad","\uffaf","\uffb0","\uffb1","\uffb2","\uffb3","\uffb4","\uffb5","\uffb6","\uffb7","\uffb8","\uffb9","\uffba","\uffbb","\uffbc","\uffbe","\uffbd","\uffbf","\uffc0","\uffc1","\uffc2","\uffc3","\uffc4","\uffc5","\uffc6","\uffc7","\uffc8","\uffc9","\uffca","\uffcb","\uffcc","\uffce","\uffcd","\uffcf","\uffd0","\uffd1","\uffd2","\uffd3","\uffd4","\uffd5","\uffd6","\uffd7","\uffd8","\uffd9","\uffda","\uffdb","\uffdc","\uffde","\uffdd","\uffdf","\uffe0","\uffe1","\uffe2","\uffe3","\uffe4","\uffe5","\uffe6","\uffe7","\uffe8","\uffe9","\uffea","\uffeb","\uffec","\uffee","\uffed","\uffef","\ufff0","\ufff1","\ufff2","\ufff3","\ufff4","\ufff5","\ufff6","\ufff7","\ufff8","\ufff9","\ufffa","\ufffb","\ufffc","\ufffe","\ufffd","\uffff",
										"\u0100","\u0101","\u0102","\u0103","\u0104","\u0105","\u0106","\u0107","\u0108","\u0109","\u010a","\u010b","\u010c","\u010e","\u010d","\u010f","\u0110","\u0111","\u0112","\u0113","\u0114","\u0115","\u0116","\u0117","\u0118","\u0119","\u011a","\u011b","\u011c","\u011e","\u011d","\u011f","\u0120","\u0121","\u0122","\u0123","\u0124","\u0125","\u0126","\u0127","\u0128","\u0129","\u012a","\u012b","\u012c","\u012e","\u012d","\u012f","\u0130","\u0131","\u0132","\u0133","\u0134","\u0135","\u0136","\u0137","\u0138","\u0139","\u013a","\u013b","\u013c","\u013e","\u013d","\u013f","\u0140","\u0141","\u0142","\u0143","\u0144","\u0145","\u0146","\u0147","\u0148","\u0149","\u014a","\u014b","\u014c","\u014e","\u014d","\u014f","\u0150","\u0151","\u0152","\u0153","\u0154","\u0155","\u0156","\u0157","\u0158","\u0159","\u015a","\u015b","\u015c","\u015e","\u015d","\u015f","\u0160","\u0161","\u0162","\u0163","\u0164","\u0165","\u0166","\u0167","\u0168","\u0169","\u016a","\u016b","\u016c","\u016e","\u016d","\u016f","\u0170","\u0171","\u0172","\u0173","\u0174","\u0175","\u0176","\u0177","\u0178","\u0179","\u017a","\u017b","\u017c","\u017e","\u017d","\u017f","\u0180","\u0181","\u0182","\u0183","\u0184","\u0185","\u0186","\u0187","\u0188","\u0189","\u018a","\u018b","\u018c","\u018e","\u018d","\u018f","\u0190","\u0191","\u0192","\u0193","\u0194","\u0195","\u0196","\u0197","\u0198","\u0199","\u019a","\u019b","\u019c","\u019e","\u019d","\u019f","\u01a0","\u01a1","\u01a2","\u01a3","\u01a4","\u01a5","\u01a6","\u01a7","\u01a8","\u01a9","\u01aa","\u01ab","\u01ac","\u01ae","\u01ad","\u01af","\u01b0","\u01b1","\u01b2","\u01b3","\u01b4","\u01b5","\u01b6","\u01b7","\u01b8","\u01b9","\u01ba","\u01bb","\u01bc","\u01be","\u01bd","\u01bf","\u01c0","\u01c1","\u01c2","\u01c3","\u01c4","\u01c5","\u01c6","\u01c7","\u01c8","\u01c9","\u01ca","\u01cb","\u01cc","\u01ce","\u01cd","\u01cf","\u01d0","\u01d1","\u01d2","\u01d3","\u01d4","\u01d5","\u01d6","\u01d7","\u01d8","\u01d9","\u01da","\u01db","\u01dc","\u01de","\u01dd","\u01df","\u01e0","\u01e1","\u01e2","\u01e3","\u01e4","\u01e5","\u01e6","\u01e7","\u01e8","\u01e9","\u01ea","\u01eb","\u01ec","\u01ee","\u01ed","\u01ef","\u01f0","\u01f1","\u01f2","\u01f3","\u01f4","\u01f5","\u01f6","\u01f7","\u01f8","\u01f9","\u01fa","\u01fb","\u01fc","\u01fe","\u01fd","\u01ff",
										"\u0300","\u0301","\u0302","\u0303","\u0304","\u0305","\u0306","\u0307","\u0308","\u0309","\u030a","\u030b","\u030c","\u030e","\u030d","\u030f","\u0310","\u0311","\u0312","\u0313","\u0314","\u0315","\u0316","\u0317","\u0318","\u0319","\u031a","\u031b","\u031c","\u031e","\u031d","\u031f","\u0320","\u0321","\u0322","\u0323","\u0324","\u0325","\u0326","\u0327","\u0328","\u0329","\u032a","\u032b","\u032c","\u032e","\u032d","\u032f","\u0330","\u0331","\u0332","\u0333","\u0334","\u0335","\u0336","\u0337","\u0338","\u0339","\u033a","\u033b","\u033c","\u033e","\u033d","\u033f","\u0340","\u0341","\u0342","\u0343","\u0344","\u0345","\u0346","\u0347","\u0348","\u0349","\u034a","\u034b","\u034c","\u034e","\u034d","\u034f","\u0350","\u0351","\u0352","\u0353","\u0354","\u0355","\u0356","\u0357","\u0358","\u0359","\u035a","\u035b","\u035c","\u035e","\u035d","\u035f","\u0360","\u0361","\u0362","\u0363","\u0364","\u0365","\u0366","\u0367","\u0368","\u0369","\u036a","\u036b","\u036c","\u036e","\u036d","\u036f","\u0370","\u0371","\u0372","\u0373","\u0374","\u0375","\u0376","\u0377","\u0378","\u0379","\u037a","\u037b","\u037c","\u037e","\u037d","\u037f","\u0380","\u0381","\u0382","\u0383","\u0384","\u0385","\u0386","\u0387","\u0388","\u0389","\u038a","\u038b","\u038c","\u038e","\u038d","\u038f","\u0390","\u0391","\u0392","\u0393","\u0394","\u0395","\u0396","\u0397","\u0398","\u0399","\u039a","\u039b","\u039c","\u039e","\u039d","\u039f","\u03a0","\u03a1","\u03a2","\u03a3","\u03a4","\u03a5","\u03a6","\u03a7","\u03a8","\u03a9","\u03aa","\u03ab","\u03ac","\u03ae","\u03ad","\u03af","\u03b0","\u03b1","\u03b2","\u03b3","\u03b4","\u03b5","\u03b6","\u03b7","\u03b8","\u03b9","\u03ba","\u03bb","\u03bc","\u03be","\u03bd","\u03bf","\u03c0","\u03c1","\u03c2","\u03c3","\u03c4","\u03c5","\u03c6","\u03c7","\u03c8","\u03c9","\u03ca","\u03cb","\u03cc","\u03ce","\u03cd","\u03cf","\u03d0","\u03d1","\u03d2","\u03d3","\u03d4","\u03d5","\u03d6","\u03d7","\u03d8","\u03d9","\u03da","\u03db","\u03dc","\u03de","\u03dd","\u03df","\u03e0","\u03e1","\u03e2","\u03e3","\u03e4","\u03e5","\u03e6","\u03e7","\u03e8","\u03e9","\u03ea","\u03eb","\u03ec","\u03ee","\u03ed","\u03ef","\u03f0","\u03f1","\u03f2","\u03f3","\u03f4","\u03f5","\u03f6","\u03f7","\u03f8","\u03f9","\u03fa","\u03fb","\u03fc","\u03fe","\u03fd","\u03ff",
										"\u0400","\u0401","\u0402","\u0403","\u0404","\u0405","\u0406","\u0407","\u0408","\u0409","\u040a","\u040b","\u040c","\u040e","\u040d","\u040f","\u0410","\u0411","\u0412","\u0413","\u0414","\u0415","\u0416","\u0417","\u0418","\u0419","\u041a","\u041b","\u041c","\u041e","\u041d","\u041f","\u0420","\u0421","\u0422","\u0423","\u0424","\u0425","\u0426","\u0427","\u0428","\u0429","\u042a","\u042b","\u042c","\u042e","\u042d","\u042f","\u0430","\u0431","\u0432","\u0433","\u0434","\u0435","\u0436","\u0437","\u0438","\u0439","\u043a","\u043b","\u043c","\u043e","\u043d","\u043f","\u0440","\u0441","\u0442","\u0443","\u0444","\u0445","\u0446","\u0447","\u0448","\u0449","\u044a","\u044b","\u044c","\u044e","\u044d","\u044f","\u0450","\u0451","\u0452","\u0453","\u0454","\u0455","\u0456","\u0457","\u0458","\u0459","\u045a","\u045b","\u045c","\u045e","\u045d","\u045f","\u0460","\u0461","\u0462","\u0463","\u0464","\u0465","\u0466","\u0467","\u0468","\u0469","\u046a","\u046b","\u046c","\u046e","\u046d","\u046f","\u0470","\u0471","\u0472","\u0473","\u0474","\u0475","\u0476","\u0477","\u0478","\u0479","\u047a","\u047b","\u047c","\u047e","\u047d","\u047f","\u0480","\u0481","\u0482","\u0483","\u0484","\u0485","\u0486","\u0487","\u0488","\u0489","\u048a","\u048b","\u048c","\u048e","\u048d","\u048f","\u0490","\u0491","\u0492","\u0493","\u0494","\u0495","\u0496","\u0497","\u0498","\u0499","\u049a","\u049b","\u049c","\u049e","\u049d","\u049f","\u04a0","\u04a1","\u04a2","\u04a3","\u04a4","\u04a5","\u04a6","\u04a7","\u04a8","\u04a9","\u04aa","\u04ab","\u04ac","\u04ae","\u04ad","\u04af","\u04b0","\u04b1","\u04b2","\u04b3","\u04b4","\u04b5","\u04b6","\u04b7","\u04b8","\u04b9","\u04ba","\u04bb","\u04bc","\u04be","\u04bd","\u04bf","\u04c0","\u04c1","\u04c2","\u04c3","\u04c4","\u04c5","\u04c6","\u04c7","\u04c8","\u04c9","\u04ca","\u04cb","\u04cc","\u04ce","\u04cd","\u04cf","\u04d0","\u04d1","\u04d2","\u04d3","\u04d4","\u04d5","\u04d6","\u04d7","\u04d8","\u04d9","\u04da","\u04db","\u04dc","\u04de","\u04dd","\u04df","\u04e0","\u04e1","\u04e2","\u04e3","\u04e4","\u04e5","\u04e6","\u04e7","\u04e8","\u04e9","\u04ea","\u04eb","\u04ec","\u04ee","\u04ed","\u04ef","\u04f0","\u04f1","\u04f2","\u04f3","\u04f4","\u04f5","\u04f6","\u04f7","\u04f8","\u04f9","\u04fa","\u04fb","\u04fc","\u04fe","\u04fd","\u04ff",
										"\u0600","\u0601","\u0602","\u0603","\u0604","\u0605","\u0606","\u0607","\u0608","\u0609","\u060a","\u060b","\u060c","\u060e","\u060d","\u060f","\u0610","\u0611","\u0612","\u0613","\u0614","\u0615","\u0616","\u0617","\u0618","\u0619","\u061a","\u061b","\u061c","\u061e","\u061d","\u061f","\u0620","\u0621","\u0622","\u0623","\u0624","\u0625","\u0626","\u0627","\u0628","\u0629","\u062a","\u062b","\u062c","\u062e","\u062d","\u062f","\u0630","\u0631","\u0632","\u0633","\u0634","\u0635","\u0636","\u0637","\u0638","\u0639","\u063a","\u063b","\u063c","\u063e","\u063d","\u063f","\u0640","\u0641","\u0642","\u0643","\u0644","\u0645","\u0646","\u0647","\u0648","\u0649","\u064a","\u064b","\u064c","\u064e","\u064d","\u064f","\u0650","\u0651","\u0652","\u0653","\u0654","\u0655","\u0656","\u0657","\u0658","\u0659","\u065a","\u065b","\u065c","\u065e","\u065d","\u065f","\u0660","\u0661","\u0662","\u0663","\u0664","\u0665","\u0666","\u0667","\u0668","\u0669","\u066a","\u066b","\u066c","\u066e","\u066d","\u066f","\u0670","\u0671","\u0672","\u0673","\u0674","\u0675","\u0676","\u0677","\u0678","\u0679","\u067a","\u067b","\u067c","\u067e","\u067d","\u067f","\u0680","\u0681","\u0682","\u0683","\u0684","\u0685","\u0686","\u0687","\u0688","\u0689","\u068a","\u068b","\u068c","\u068e","\u068d","\u068f","\u0690","\u0691","\u0692","\u0693","\u0694","\u0695","\u0696","\u0697","\u0698","\u0699","\u069a","\u069b","\u069c","\u069e","\u069d","\u069f","\u06a0","\u06a1","\u06a2","\u06a3","\u06a4","\u06a5","\u06a6","\u06a7","\u06a8","\u06a9","\u06aa","\u06ab","\u06ac","\u06ae","\u06ad","\u06af","\u06b0","\u06b1","\u06b2","\u06b3","\u06b4","\u06b5","\u06b6","\u06b7","\u06b8","\u06b9","\u06ba","\u06bb","\u06bc","\u06be","\u06bd","\u06bf","\u06c0","\u06c1","\u06c2","\u06c3","\u06c4","\u06c5","\u06c6","\u06c7","\u06c8","\u06c9","\u06ca","\u06cb","\u06cc","\u06ce","\u06cd","\u06cf","\u06d0","\u06d1","\u06d2","\u06d3","\u06d4","\u06d5","\u06d6","\u06d7","\u06d8","\u06d9","\u06da","\u06db","\u06dc","\u06de","\u06dd","\u06df","\u06e0","\u06e1","\u06e2","\u06e3","\u06e4","\u06e5","\u06e6","\u06e7","\u06e8","\u06e9","\u06ea","\u06eb","\u06ec","\u06ee","\u06ed","\u06ef","\u06f0","\u06f1","\u06f2","\u06f3","\u06f4","\u06f5","\u06f6","\u06f7","\u06f8","\u06f9","\u06fa","\u06fb","\u06fc","\u06fe","\u06fd","\u06ff",
										"\u1200","\u1201","\u1202","\u1203","\u1204","\u1205","\u1206","\u1207","\u1208","\u1209","\u120a","\u120b","\u120c","\u120e","\u120d","\u120f","\u1210","\u1211","\u1212","\u1213","\u1214","\u1215","\u1216","\u1217","\u1218","\u1219","\u121a","\u121b","\u121c","\u121e","\u121d","\u121f","\u1220","\u1221","\u1222","\u1223","\u1224","\u1225","\u1226","\u1227","\u1228","\u1229","\u122a","\u122b","\u122c","\u122e","\u122d","\u122f","\u1230","\u1231","\u1232","\u1233","\u1234","\u1235","\u1236","\u1237","\u1238","\u1239","\u123a","\u123b","\u123c","\u123e","\u123d","\u123f","\u1240","\u1241","\u1242","\u1243","\u1244","\u1245","\u1246","\u1247","\u1248","\u1249","\u124a","\u124b","\u124c","\u124e","\u124d","\u124f","\u1250","\u1251","\u1252","\u1253","\u1254","\u1255","\u1256","\u1257","\u1258","\u1259","\u125a","\u125b","\u125c","\u125e","\u125d","\u125f","\u1260","\u1261","\u1262","\u1263","\u1264","\u1265","\u1266","\u1267","\u1268","\u1269","\u126a","\u126b","\u126c","\u126e","\u126d","\u126f","\u1270","\u1271","\u1272","\u1273","\u1274","\u1275","\u1276","\u1277","\u1278","\u1279","\u127a","\u127b","\u127c","\u127e","\u127d","\u127f","\u1280","\u1281","\u1282","\u1283","\u1284","\u1285","\u1286","\u1287","\u1288","\u1289","\u128a","\u128b","\u128c","\u128e","\u128d","\u128f","\u1290","\u1291","\u1292","\u1293","\u1294","\u1295","\u1296","\u1297","\u1298","\u1299","\u129a","\u129b","\u129c","\u129e","\u129d","\u129f","\u12a0","\u12a1","\u12a2","\u12a3","\u12a4","\u12a5","\u12a6","\u12a7","\u12a8","\u12a9","\u12aa","\u12ab","\u12ac","\u12ae","\u12ad","\u12af","\u12b0","\u12b1","\u12b2","\u12b3","\u12b4","\u12b5","\u12b6","\u12b7","\u12b8","\u12b9","\u12ba","\u12bb","\u12bc","\u12be","\u12bd","\u12bf","\u12c0","\u12c1","\u12c2","\u12c3","\u12c4","\u12c5","\u12c6","\u12c7","\u12c8","\u12c9","\u12ca","\u12cb","\u12cc","\u12ce","\u12cd","\u12cf","\u12d0","\u12d1","\u12d2","\u12d3","\u12d4","\u12d5","\u12d6","\u12d7","\u12d8","\u12d9","\u12da","\u12db","\u12dc","\u12de","\u12dd","\u12df","\u12e0","\u12e1","\u12e2","\u12e3","\u12e4","\u12e5","\u12e6","\u12e7","\u12e8","\u12e9","\u12ea","\u12eb","\u12ec","\u12ee","\u12ed","\u12ef","\u12f0","\u12f1","\u12f2","\u12f3","\u12f4","\u12f5","\u12f6","\u12f7","\u12f8","\u12f9","\u12fa","\u12fb","\u12fc","\u12fe","\u12fd","\u12ff",
										"\u0e00","\u0e01","\u0e02","\u0e03","\u0e04","\u0e05","\u0e06","\u0e07","\u0e08","\u0e09","\u0e0a","\u0e0b","\u0e0c","\u0e0e","\u0e0d","\u0e0f","\u0e10","\u0e11","\u0e12","\u0e13","\u0e14","\u0e15","\u0e16","\u0e17","\u0e18","\u0e19","\u0e1a","\u0e1b","\u0e1c","\u0e1e","\u0e1d","\u0e1f","\u0e20","\u0e21","\u0e22","\u0e23","\u0e24","\u0e25","\u0e26","\u0e27","\u0e28","\u0e29","\u0e2a","\u0e2b","\u0e2c","\u0e2e","\u0e2d","\u0e2f","\u0e30","\u0e31","\u0e32","\u0e33","\u0e34","\u0e35","\u0e36","\u0e37","\u0e38","\u0e39","\u0e3a","\u0e3b","\u0e3c","\u0e3e","\u0e3d","\u0e3f","\u0e40","\u0e41","\u0e42","\u0e43","\u0e44","\u0e45","\u0e46","\u0e47","\u0e48","\u0e49","\u0e4a","\u0e4b","\u0e4c","\u0e4e","\u0e4d","\u0e4f","\u0e50","\u0e51","\u0e52","\u0e53","\u0e54","\u0e55","\u0e56","\u0e57","\u0e58","\u0e59","\u0e5a","\u0e5b","\u0e5c","\u0e5e","\u0e5d","\u0e5f","\u0e60","\u0e61","\u0e62","\u0e63","\u0e64","\u0e65","\u0e66","\u0e67","\u0e68","\u0e69","\u0e6a","\u0e6b","\u0e6c","\u0e6e","\u0e6d","\u0e6f","\u0e70","\u0e71","\u0e72","\u0e73","\u0e74","\u0e75","\u0e76","\u0e77","\u0e78","\u0e79","\u0e7a","\u0e7b","\u0e7c","\u0e7e","\u0e7d","\u0e7f","\u0e80","\u0e81","\u0e82","\u0e83","\u0e84","\u0e85","\u0e86","\u0e87","\u0e88","\u0e89","\u0e8a","\u0e8b","\u0e8c","\u0e8e","\u0e8d","\u0e8f","\u0e90","\u0e91","\u0e92","\u0e93","\u0e94","\u0e95","\u0e96","\u0e97","\u0e98","\u0e99","\u0e9a","\u0e9b","\u0e9c","\u0e9e","\u0e9d","\u0e9f","\u0ea0","\u0ea1","\u0ea2","\u0ea3","\u0ea4","\u0ea5","\u0ea6","\u0ea7","\u0ea8","\u0ea9","\u0eaa","\u0eab","\u0eac","\u0eae","\u0ead","\u0eaf","\u0eb0","\u0eb1","\u0eb2","\u0eb3","\u0eb4","\u0eb5","\u0eb6","\u0eb7","\u0eb8","\u0eb9","\u0eba","\u0ebb","\u0ebc","\u0ebe","\u0ebd","\u0ebf","\u0ec0","\u0ec1","\u0ec2","\u0ec3","\u0ec4","\u0ec5","\u0ec6","\u0ec7","\u0ec8","\u0ec9","\u0eca","\u0ecb","\u0ecc","\u0ece","\u0ecd","\u0ecf","\u0ed0","\u0ed1","\u0ed2","\u0ed3","\u0ed4","\u0ed5","\u0ed6","\u0ed7","\u0ed8","\u0ed9","\u0eda","\u0edb","\u0edc","\u0ede","\u0edd","\u0edf","\u0ee0","\u0ee1","\u0ee2","\u0ee3","\u0ee4","\u0ee5","\u0ee6","\u0ee7","\u0ee8","\u0ee9","\u0eea","\u0eeb","\u0eec","\u0eee","\u0eed","\u0eef","\u0ef0","\u0ef1","\u0ef2","\u0ef3","\u0ef4","\u0ef5","\u0ef6","\u0ef7","\u0ef8","\u0ef9","\u0efa","\u0efb","\u0efc","\u0efe","\u0efd","\u0eff",
										"\u1400","\u1401","\u1402","\u1403","\u1404","\u1405","\u1406","\u1407","\u1408","\u1409","\u140a","\u140b","\u140c","\u140e","\u140d","\u140f","\u1410","\u1411","\u1412","\u1413","\u1414","\u1415","\u1416","\u1417","\u1418","\u1419","\u141a","\u141b","\u141c","\u141e","\u141d","\u141f","\u1420","\u1421","\u1422","\u1423","\u1424","\u1425","\u1426","\u1427","\u1428","\u1429","\u142a","\u142b","\u142c","\u142e","\u142d","\u142f","\u1430","\u1431","\u1432","\u1433","\u1434","\u1435","\u1436","\u1437","\u1438","\u1439","\u143a","\u143b","\u143c","\u143e","\u143d","\u143f","\u1440","\u1441","\u1442","\u1443","\u1444","\u1445","\u1446","\u1447","\u1448","\u1449","\u144a","\u144b","\u144c","\u144e","\u144d","\u144f","\u1450","\u1451","\u1452","\u1453","\u1454","\u1455","\u1456","\u1457","\u1458","\u1459","\u145a","\u145b","\u145c","\u145e","\u145d","\u145f","\u1460","\u1461","\u1462","\u1463","\u1464","\u1465","\u1466","\u1467","\u1468","\u1469","\u146a","\u146b","\u146c","\u146e","\u146d","\u146f","\u1470","\u1471","\u1472","\u1473","\u1474","\u1475","\u1476","\u1477","\u1478","\u1479","\u147a","\u147b","\u147c","\u147e","\u147d","\u147f","\u1480","\u1481","\u1482","\u1483","\u1484","\u1485","\u1486","\u1487","\u1488","\u1489","\u148a","\u148b","\u148c","\u148e","\u148d","\u148f","\u1490","\u1491","\u1492","\u1493","\u1494","\u1495","\u1496","\u1497","\u1498","\u1499","\u149a","\u149b","\u149c","\u149e","\u149d","\u149f","\u14a0","\u14a1","\u14a2","\u14a3","\u14a4","\u14a5","\u14a6","\u14a7","\u14a8","\u14a9","\u14aa","\u14ab","\u14ac","\u14ae","\u14ad","\u14af","\u14b0","\u14b1","\u14b2","\u14b3","\u14b4","\u14b5","\u14b6","\u14b7","\u14b8","\u14b9","\u14ba","\u14bb","\u14bc","\u14be","\u14bd","\u14bf","\u14c0","\u14c1","\u14c2","\u14c3","\u14c4","\u14c5","\u14c6","\u14c7","\u14c8","\u14c9","\u14ca","\u14cb","\u14cc","\u14ce","\u14cd","\u14cf","\u14d0","\u14d1","\u14d2","\u14d3","\u14d4","\u14d5","\u14d6","\u14d7","\u14d8","\u14d9","\u14da","\u14db","\u14dc","\u14de","\u14dd","\u14df","\u14e0","\u14e1","\u14e2","\u14e3","\u14e4","\u14e5","\u14e6","\u14e7","\u14e8","\u14e9","\u14ea","\u14eb","\u14ec","\u14ee","\u14ed","\u14ef","\u14f0","\u14f1","\u14f2","\u14f3","\u14f4","\u14f5","\u14f6","\u14f7","\u14f8","\u14f9","\u14fa","\u14fb","\u14fc","\u14fe","\u14fd","\u14ff",
										"\u2100","\u2101","\u2102","\u2103","\u2104","\u2105","\u2106","\u2107","\u2108","\u2109","\u210a","\u210b","\u210c","\u210e","\u210d","\u210f","\u2110","\u2111","\u2112","\u2113","\u2114","\u2115","\u2116","\u2117","\u2118","\u2119","\u211a","\u211b","\u211c","\u211e","\u211d","\u211f","\u2120","\u2121","\u2122","\u2123","\u2124","\u2125","\u2126","\u2127","\u2128","\u2129","\u212a","\u212b","\u212c","\u212e","\u212d","\u212f","\u2130","\u2131","\u2132","\u2133","\u2134","\u2135","\u2136","\u2137","\u2138","\u2139","\u213a","\u213b","\u213c","\u213e","\u213d","\u213f","\u2140","\u2141","\u2142","\u2143","\u2144","\u2145","\u2146","\u2147","\u2148","\u2149","\u214a","\u214b","\u214c","\u214e","\u214d","\u214f","\u2150","\u2151","\u2152","\u2153","\u2154","\u2155","\u2156","\u2157","\u2158","\u2159","\u215a","\u215b","\u215c","\u215e","\u215d","\u215f","\u2160","\u2161","\u2162","\u2163","\u2164","\u2165","\u2166","\u2167","\u2168","\u2169","\u216a","\u216b","\u216c","\u216e","\u216d","\u216f","\u2170","\u2171","\u2172","\u2173","\u2174","\u2175","\u2176","\u2177","\u2178","\u2179","\u217a","\u217b","\u217c","\u217e","\u217d","\u217f","\u2180","\u2181","\u2182","\u2183","\u2184","\u2185","\u2186","\u2187","\u2188","\u2189","\u218a","\u218b","\u218c","\u218e","\u218d","\u218f","\u2190","\u2191","\u2192","\u2193","\u2194","\u2195","\u2196","\u2197","\u2198","\u2199","\u219a","\u219b","\u219c","\u219e","\u219d","\u219f","\u21a0","\u21a1","\u21a2","\u21a3","\u21a4","\u21a5","\u21a6","\u21a7","\u21a8","\u21a9","\u21aa","\u21ab","\u21ac","\u21ae","\u21ad","\u21af","\u21b0","\u21b1","\u21b2","\u21b3","\u21b4","\u21b5","\u21b6","\u21b7","\u21b8","\u21b9","\u21ba","\u21bb","\u21bc","\u21be","\u21bd","\u21bf","\u21c0","\u21c1","\u21c2","\u21c3","\u21c4","\u21c5","\u21c6","\u21c7","\u21c8","\u21c9","\u21ca","\u21cb","\u21cc","\u21ce","\u21cd","\u21cf","\u21d0","\u21d1","\u21d2","\u21d3","\u21d4","\u21d5","\u21d6","\u21d7","\u21d8","\u21d9","\u21da","\u21db","\u21dc","\u21de","\u21dd","\u21df","\u21e0","\u21e1","\u21e2","\u21e3","\u21e4","\u21e5","\u21e6","\u21e7","\u21e8","\u21e9","\u21ea","\u21eb","\u21ec","\u21ee","\u21ed","\u21ef","\u21f0","\u21f1","\u21f2","\u21f3","\u21f4","\u21f5","\u21f6","\u21f7","\u21f8","\u21f9","\u21fa","\u21fb","\u21fc","\u21fe","\u21fd","\u21ff",
										"\u2200","\u2201","\u2202","\u2203","\u2204","\u2205","\u2206","\u2207","\u2208","\u2209","\u220a","\u220b","\u220c","\u220e","\u220d","\u220f","\u2210","\u2211","\u2212","\u2213","\u2214","\u2215","\u2216","\u2217","\u2218","\u2219","\u221a","\u221b","\u221c","\u221e","\u221d","\u221f","\u2220","\u2221","\u2222","\u2223","\u2224","\u2225","\u2226","\u2227","\u2228","\u2229","\u222a","\u222b","\u222c","\u222e","\u222d","\u222f","\u2230","\u2231","\u2232","\u2233","\u2234","\u2235","\u2236","\u2237","\u2238","\u2239","\u223a","\u223b","\u223c","\u223e","\u223d","\u223f","\u2240","\u2241","\u2242","\u2243","\u2244","\u2245","\u2246","\u2247","\u2248","\u2249","\u224a","\u224b","\u224c","\u224e","\u224d","\u224f","\u2250","\u2251","\u2252","\u2253","\u2254","\u2255","\u2256","\u2257","\u2258","\u2259","\u225a","\u225b","\u225c","\u225e","\u225d","\u225f","\u2260","\u2261","\u2262","\u2263","\u2264","\u2265","\u2266","\u2267","\u2268","\u2269","\u226a","\u226b","\u226c","\u226e","\u226d","\u226f","\u2270","\u2271","\u2272","\u2273","\u2274","\u2275","\u2276","\u2277","\u2278","\u2279","\u227a","\u227b","\u227c","\u227e","\u227d","\u227f","\u2280","\u2281","\u2282","\u2283","\u2284","\u2285","\u2286","\u2287","\u2288","\u2289","\u228a","\u228b","\u228c","\u228e","\u228d","\u228f","\u2290","\u2291","\u2292","\u2293","\u2294","\u2295","\u2296","\u2297","\u2298","\u2299","\u229a","\u229b","\u229c","\u229e","\u229d","\u229f","\u22a0","\u22a1","\u22a2","\u22a3","\u22a4","\u22a5","\u22a6","\u22a7","\u22a8","\u22a9","\u22aa","\u22ab","\u22ac","\u22ae","\u22ad","\u22af","\u22b0","\u22b1","\u22b2","\u22b3","\u22b4","\u22b5","\u22b6","\u22b7","\u22b8","\u22b9","\u22ba","\u22bb","\u22bc","\u22be","\u22bd","\u22bf","\u22c0","\u22c1","\u22c2","\u22c3","\u22c4","\u22c5","\u22c6","\u22c7","\u22c8","\u22c9","\u22ca","\u22cb","\u22cc","\u22ce","\u22cd","\u22cf","\u22d0","\u22d1","\u22d2","\u22d3","\u22d4","\u22d5","\u22d6","\u22d7","\u22d8","\u22d9","\u22da","\u22db","\u22dc","\u22de","\u22dd","\u22df","\u22e0","\u22e1","\u22e2","\u22e3","\u22e4","\u22e5","\u22e6","\u22e7","\u22e8","\u22e9","\u22ea","\u22eb","\u22ec","\u22ee","\u22ed","\u22ef","\u22f0","\u22f1","\u22f2","\u22f3","\u22f4","\u22f5","\u22f6","\u22f7","\u22f8","\u22f9","\u22fa","\u22fb","\u22fc","\u22fe","\u22fd","\u22ff",
										"\u2400","\u2401","\u2402","\u2403","\u2404","\u2405","\u2406","\u2407","\u2408","\u2409","\u240a","\u240b","\u240c","\u240e","\u240d","\u240f","\u2410","\u2411","\u2412","\u2413","\u2414","\u2415","\u2416","\u2417","\u2418","\u2419","\u241a","\u241b","\u241c","\u241e","\u241d","\u241f","\u2420","\u2421","\u2422","\u2423","\u2424","\u2425","\u2426","\u2427","\u2428","\u2429","\u242a","\u242b","\u242c","\u242e","\u242d","\u242f","\u2430","\u2431","\u2432","\u2433","\u2434","\u2435","\u2436","\u2437","\u2438","\u2439","\u243a","\u243b","\u243c","\u243e","\u243d","\u243f","\u2440","\u2441","\u2442","\u2443","\u2444","\u2445","\u2446","\u2447","\u2448","\u2449","\u244a","\u244b","\u244c","\u244e","\u244d","\u244f","\u2450","\u2451","\u2452","\u2453","\u2454","\u2455","\u2456","\u2457","\u2458","\u2459","\u245a","\u245b","\u245c","\u245e","\u245d","\u245f","\u2460","\u2461","\u2462","\u2463","\u2464","\u2465","\u2466","\u2467","\u2468","\u2469","\u246a","\u246b","\u246c","\u246e","\u246d","\u246f","\u2470","\u2471","\u2472","\u2473","\u2474","\u2475","\u2476","\u2477","\u2478","\u2479","\u247a","\u247b","\u247c","\u247e","\u247d","\u247f","\u2480","\u2481","\u2482","\u2483","\u2484","\u2485","\u2486","\u2487","\u2488","\u2489","\u248a","\u248b","\u248c","\u248e","\u248d","\u248f","\u2490","\u2491","\u2492","\u2493","\u2494","\u2495","\u2496","\u2497","\u2498","\u2499","\u249a","\u249b","\u249c","\u249e","\u249d","\u249f","\u24a0","\u24a1","\u24a2","\u24a3","\u24a4","\u24a5","\u24a6","\u24a7","\u24a8","\u24a9","\u24aa","\u24ab","\u24ac","\u24ae","\u24ad","\u24af","\u24b0","\u24b1","\u24b2","\u24b3","\u24b4","\u24b5","\u24b6","\u24b7","\u24b8","\u24b9","\u24ba","\u24bb","\u24bc","\u24be","\u24bd","\u24bf","\u24c0","\u24c1","\u24c2","\u24c3","\u24c4","\u24c5","\u24c6","\u24c7","\u24c8","\u24c9","\u24ca","\u24cb","\u24cc","\u24ce","\u24cd","\u24cf","\u24d0","\u24d1","\u24d2","\u24d3","\u24d4","\u24d5","\u24d6","\u24d7","\u24d8","\u24d9","\u24da","\u24db","\u24dc","\u24de","\u24dd","\u24df","\u24e0","\u24e1","\u24e2","\u24e3","\u24e4","\u24e5","\u24e6","\u24e7","\u24e8","\u24e9","\u24ea","\u24eb","\u24ec","\u24ee","\u24ed","\u24ef","\u24f0","\u24f1","\u24f2","\u24f3","\u24f4","\u24f5","\u24f6","\u24f7","\u24f8","\u24f9","\u24fa","\u24fb","\u24fc","\u24fe","\u24fd","\u24ff",
										"\u2500","\u2501","\u2502","\u2503","\u2504","\u2505","\u2506","\u2507","\u2508","\u2509","\u250a","\u250b","\u250c","\u250e","\u250d","\u250f","\u2510","\u2511","\u2512","\u2513","\u2514","\u2515","\u2516","\u2517","\u2518","\u2519","\u251a","\u251b","\u251c","\u251e","\u251d","\u251f","\u2520","\u2521","\u2522","\u2523","\u2524","\u2525","\u2526","\u2527","\u2528","\u2529","\u252a","\u252b","\u252c","\u252e","\u252d","\u252f","\u2530","\u2531","\u2532","\u2533","\u2534","\u2535","\u2536","\u2537","\u2538","\u2539","\u253a","\u253b","\u253c","\u253e","\u253d","\u253f","\u2540","\u2541","\u2542","\u2543","\u2544","\u2545","\u2546","\u2547","\u2548","\u2549","\u254a","\u254b","\u254c","\u254e","\u254d","\u254f","\u2550","\u2551","\u2552","\u2553","\u2554","\u2555","\u2556","\u2557","\u2558","\u2559","\u255a","\u255b","\u255c","\u255e","\u255d","\u255f","\u2560","\u2561","\u2562","\u2563","\u2564","\u2565","\u2566","\u2567","\u2568","\u2569","\u256a","\u256b","\u256c","\u256e","\u256d","\u256f","\u2570","\u2571","\u2572","\u2573","\u2574","\u2575","\u2576","\u2577","\u2578","\u2579","\u257a","\u257b","\u257c","\u257e","\u257d","\u257f","\u2580","\u2581","\u2582","\u2583","\u2584","\u2585","\u2586","\u2587","\u2588","\u2589","\u258a","\u258b","\u258c","\u258e","\u258d","\u258f","\u2590","\u2591","\u2592","\u2593","\u2594","\u2595","\u2596","\u2597","\u2598","\u2599","\u259a","\u259b","\u259c","\u259e","\u259d","\u259f","\u25a0","\u25a1","\u25a2","\u25a3","\u25a4","\u25a5","\u25a6","\u25a7","\u25a8","\u25a9","\u25aa","\u25ab","\u25ac","\u25ae","\u25ad","\u25af","\u25b0","\u25b1","\u25b2","\u25b3","\u25b4","\u25b5","\u25b6","\u25b7","\u25b8","\u25b9","\u25ba","\u25bb","\u25bc","\u25be","\u25bd","\u25bf","\u25c0","\u25c1","\u25c2","\u25c3","\u25c4","\u25c5","\u25c6","\u25c7","\u25c8","\u25c9","\u25ca","\u25cb","\u25cc","\u25ce","\u25cd","\u25cf","\u25d0","\u25d1","\u25d2","\u25d3","\u25d4","\u25d5","\u25d6","\u25d7","\u25d8","\u25d9","\u25da","\u25db","\u25dc","\u25de","\u25dd","\u25df","\u25e0","\u25e1","\u25e2","\u25e3","\u25e4","\u25e5","\u25e6","\u25e7","\u25e8","\u25e9","\u25ea","\u25eb","\u25ec","\u25ee","\u25ed","\u25ef","\u25f0","\u25f1","\u25f2","\u25f3","\u25f4","\u25f5","\u25f6","\u25f7","\u25f8","\u25f9","\u25fa","\u25fb","\u25fc","\u25fe","\u25fd","\u25ff",
										"\u2600","\u2601","\u2602","\u2603","\u2604","\u2605","\u2606","\u2607","\u2608","\u2609","\u260a","\u260b","\u260c","\u260e","\u260d","\u260f","\u2610","\u2611","\u2612","\u2613","\u2614","\u2615","\u2616","\u2617","\u2618","\u2619","\u261a","\u261b","\u261c","\u261e","\u261d","\u261f","\u2620","\u2621","\u2622","\u2623","\u2624","\u2625","\u2626","\u2627","\u2628","\u2629","\u262a","\u262b","\u262c","\u262e","\u262d","\u262f","\u2630","\u2631","\u2632","\u2633","\u2634","\u2635","\u2636","\u2637","\u2638","\u2639","\u263a","\u263b","\u263c","\u263e","\u263d","\u263f","\u2640","\u2641","\u2642","\u2643","\u2644","\u2645","\u2646","\u2647","\u2648","\u2649","\u264a","\u264b","\u264c","\u264e","\u264d","\u264f","\u2650","\u2651","\u2652","\u2653","\u2654","\u2655","\u2656","\u2657","\u2658","\u2659","\u265a","\u265b","\u265c","\u265e","\u265d","\u265f","\u2660","\u2661","\u2662","\u2663","\u2664","\u2665","\u2666","\u2667","\u2668","\u2669","\u266a","\u266b","\u266c","\u266e","\u266d","\u266f","\u2670","\u2671","\u2672","\u2673","\u2674","\u2675","\u2676","\u2677","\u2678","\u2679","\u267a","\u267b","\u267c","\u267e","\u267d","\u267f","\u2680","\u2681","\u2682","\u2683","\u2684","\u2685","\u2686","\u2687","\u2688","\u2689","\u268a","\u268b","\u268c","\u268e","\u268d","\u268f","\u2690","\u2691","\u2692","\u2693","\u2694","\u2695","\u2696","\u2697","\u2698","\u2699","\u269a","\u269b","\u269c","\u269e","\u269d","\u269f","\u26a0","\u26a1","\u26a2","\u26a3","\u26a4","\u26a5","\u26a6","\u26a7","\u26a8","\u26a9","\u26aa","\u26ab","\u26ac","\u26ae","\u26ad","\u26af","\u26b0","\u26b1","\u26b2","\u26b3","\u26b4","\u26b5","\u26b6","\u26b7","\u26b8","\u26b9","\u26ba","\u26bb","\u26bc","\u26be","\u26bd","\u26bf","\u26c0","\u26c1","\u26c2","\u26c3","\u26c4","\u26c5","\u26c6","\u26c7","\u26c8","\u26c9","\u26ca","\u26cb","\u26cc","\u26ce","\u26cd","\u26cf","\u26d0","\u26d1","\u26d2","\u26d3","\u26d4","\u26d5","\u26d6","\u26d7","\u26d8","\u26d9","\u26da","\u26db","\u26dc","\u26de","\u26dd","\u26df","\u26e0","\u26e1","\u26e2","\u26e3","\u26e4","\u26e5","\u26e6","\u26e7","\u26e8","\u26e9","\u26ea","\u26eb","\u26ec","\u26ee","\u26ed","\u26ef","\u26f0","\u26f1","\u26f2","\u26f3","\u26f4","\u26f5","\u26f6","\u26f7","\u26f8","\u26f9","\u26fa","\u26fb","\u26fc","\u26fe","\u26fd","\u26ff",
										"\u2f00","\u2f01","\u2f02","\u2f03","\u2f04","\u2f05","\u2f06","\u2f07","\u2f08","\u2f09","\u2f0a","\u2f0b","\u2f0c","\u2f0e","\u2f0d","\u2f0f","\u2f10","\u2f11","\u2f12","\u2f13","\u2f14","\u2f15","\u2f16","\u2f17","\u2f18","\u2f19","\u2f1a","\u2f1b","\u2f1c","\u2f1e","\u2f1d","\u2f1f","\u2f20","\u2f21","\u2f22","\u2f23","\u2f24","\u2f25","\u2f26","\u2f27","\u2f28","\u2f29","\u2f2a","\u2f2b","\u2f2c","\u2f2e","\u2f2d","\u2f2f","\u2f30","\u2f31","\u2f32","\u2f33","\u2f34","\u2f35","\u2f36","\u2f37","\u2f38","\u2f39","\u2f3a","\u2f3b","\u2f3c","\u2f3e","\u2f3d","\u2f3f","\u2f40","\u2f41","\u2f42","\u2f43","\u2f44","\u2f45","\u2f46","\u2f47","\u2f48","\u2f49","\u2f4a","\u2f4b","\u2f4c","\u2f4e","\u2f4d","\u2f4f","\u2f50","\u2f51","\u2f52","\u2f53","\u2f54","\u2f55","\u2f56","\u2f57","\u2f58","\u2f59","\u2f5a","\u2f5b","\u2f5c","\u2f5e","\u2f5d","\u2f5f","\u2f60","\u2f61","\u2f62","\u2f63","\u2f64","\u2f65","\u2f66","\u2f67","\u2f68","\u2f69","\u2f6a","\u2f6b","\u2f6c","\u2f6e","\u2f6d","\u2f6f","\u2f70","\u2f71","\u2f72","\u2f73","\u2f74","\u2f75","\u2f76","\u2f77","\u2f78","\u2f79","\u2f7a","\u2f7b","\u2f7c","\u2f7e","\u2f7d","\u2f7f","\u2f80","\u2f81","\u2f82","\u2f83","\u2f84","\u2f85","\u2f86","\u2f87","\u2f88","\u2f89","\u2f8a","\u2f8b","\u2f8c","\u2f8e","\u2f8d","\u2f8f","\u2f90","\u2f91","\u2f92","\u2f93","\u2f94","\u2f95","\u2f96","\u2f97","\u2f98","\u2f99","\u2f9a","\u2f9b","\u2f9c","\u2f9e","\u2f9d","\u2f9f","\u2fa0","\u2fa1","\u2fa2","\u2fa3","\u2fa4","\u2fa5","\u2fa6","\u2fa7","\u2fa8","\u2fa9","\u2faa","\u2fab","\u2fac","\u2fae","\u2fad","\u2faf","\u2fb0","\u2fb1","\u2fb2","\u2fb3","\u2fb4","\u2fb5","\u2fb6","\u2fb7","\u2fb8","\u2fb9","\u2fba","\u2fbb","\u2fbc","\u2fbe","\u2fbd","\u2fbf","\u2fc0","\u2fc1","\u2fc2","\u2fc3","\u2fc4","\u2fc5","\u2fc6","\u2fc7","\u2fc8","\u2fc9","\u2fca","\u2fcb","\u2fcc","\u2fce","\u2fcd","\u2fcf","\u2fd0","\u2fd1","\u2fd2","\u2fd3","\u2fd4","\u2fd5","\u2fd6","\u2fd7","\u2fd8","\u2fd9","\u2fda","\u2fdb","\u2fdc","\u2fde","\u2fdd","\u2fdf","\u2fe0","\u2fe1","\u2fe2","\u2fe3","\u2fe4","\u2fe5","\u2fe6","\u2fe7","\u2fe8","\u2fe9","\u2fea","\u2feb","\u2fec","\u2fee","\u2fed","\u2fef","\u2ff0","\u2ff1","\u2ff2","\u2ff3","\u2ff4","\u2ff5","\u2ff6","\u2ff7","\u2ff8","\u2ff9","\u2ffa","\u2ffb","\u2ffc","\u2ffe","\u2ffd","\u2fff" };



























}


