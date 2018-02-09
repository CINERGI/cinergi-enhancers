package org.neuinfo.foundry.enhancers.common;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
/**
 * Created by valentine
 * based on code from kartik
 */

public class TemporalNLP {

    private  static StanfordCoreNLP coreNLP;

    private final static Logger logger = Logger.getLogger(TemporalNLP.class);
    private ArrayList<String> existingTemporalExtents;

    static TemporalNLP NULL_TIME = new TemporalNLP();

    private static ArrayList<String> categories=new<String>ArrayList();
    private static ArrayList<Integer> timerefs=new ArrayList<Integer>();
    private static Boolean hasday(String date){
        return date.length() > 7;
    }
    private static Boolean hasmonth(String date){
        return date.length() >= 5;
    }
    private static Boolean hasyear(String date){
        return !date.substring(0, 4).contains("X");
    }


    public TemporalNLP() {
        long startTime = System.currentTimeMillis();
        System.out.println(System.getProperty("user.dir"));
        Properties props = new Properties();

        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,");

        //props.setProperty("ner.useSUTime", "0");
        this.coreNLP = new StanfordCoreNLP(props);
       // this.coreNLP.addAnnotator(new PTBTokenizerAnnotator(false));
        this.coreNLP.addAnnotator(new TimeAnnotator("sutime",props));
        this.coreNLP.addAnnotator(new TokenizerAnnotator(false));
        this.coreNLP.addAnnotator(new WordsToSentencesAnnotator(false));
        this.coreNLP.addAnnotator(new POSTaggerAnnotator(false));
        props.setProperty("sutime.markTimeRanges", "true");
        props.setProperty("sutime.includeRange", "true");
        this.coreNLP.addAnnotator(new TimeAnnotator("sutime", props));
    }
    public StanfordCoreNLP getCoreNLP(){
        if (coreNLP != null){
            return coreNLP;
        } else {
             return null;
        }
    }
    public   List<Temporal> getDates(String txt){
        List<Temporal> temporalList = new ArrayList<Temporal>();;

        List<CoreMap> timexAnnsAll = getdates( txt);

        for (CoreMap cm : timexAnnsAll) {
            Temporal t = new Temporal();

            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            t.setName(cm.get(TimeExpression.Annotation.class).getText());
            t.setIsoString(cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());
            SUTime.Time timeSu = cm.get(TimeExpression.Annotation.class).getTemporal().getTime();

            String type = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexType().toString();
t.setOffsetStart(tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
t.setOffsetEnd(tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
            switch (type){
                case "DURATION":
                  SUTime.Range rng = cm.get(TimeExpression.Annotation.class).getTemporal().getRange();
                    //t.setStartDate(timeSu.toISOString());
                    t.setStartDate(rng.begin().toISOString());
                    t.setEndDate(rng.end().toISOString());
                    t.setDuration(rng.getDuration().toISOString());
                    // case
                    t.setDateType(dateType.DURATION);
                 break;
                case "DATE":

                    t.setStartDate(timeSu.toISOString());

                    // case
                    t.setDateType(dateType.TimeInstant);
                    break;
                case "TIME":
                    t.setStartDate(timeSu.toISOString());
                    t.setDateType(dateType.TimeInstant);
                    t.setName(timeSu.toISOString()); // label just shows time, and no date
                    break;
                default:
                    t.setStartDate(timeSu.toISOString());
                    t.setDateType(dateType.UNKNOWN);
                    break;
            }


           // t.setStartDate(cm.get(TimeExpression.Annotation.class).getTemporal().getTime().getJodaTimeInstant() );
            temporalList.add(t);
            //t.setStartDate(cm.);
            System.out.println(cm + " [from char offset " +
                    tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                    " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                    " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
        }
        System.out.println("--");
        //  }
        return temporalList;
    }

    public   List<CoreMap> getdates(String txt){

        //for (String text : args) {
            Annotation annotation = new Annotation(txt);
            annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
            this.coreNLP.annotate(annotation);
            System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
            List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
            for (CoreMap cm : timexAnnsAll) {
                List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
                System.out.println(cm + " [from char offset " +
                        tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                        " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                        " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
            }
            System.out.println("--");
      //  }
        return timexAnnsAll;
}
    public static String getLabel(CoreMap cm){
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        String label = cm + " [from char offset " +
                tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                " --> " + cm.get(TimeExpression.Annotation.class).getTemporal();
        return label;
    }
    public  static ArrayList getdates(String txt,StanfordCoreNLP pipeline){
        //start of the word analyzing part
        String text = txt;
        System.out.println("before trimming:"+text);
        // create an empty Annotation just with the given text

        ArrayList <Integer> dateranks= new <Integer>ArrayList(); //3 is only year found, 4 is only year+month, and 5 is a full date

        Boolean wordafterdate=false;
        Boolean lookingformonth=true;
        int timeref=0;
        Boolean saveword=false;
        String rangedate="";
        String monthsave="";
        String category=null;
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        StringBuilder datestring= new StringBuilder(" ");
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println("word: " + word + " pos: " + pos + " type:" + ne);
                if(category==null){
                    category=classify(word);
                }
                if(saveword &&  (! word.matches(".*[a-z].*") )){
                    rangedate=word;
                    System.out.println("saving this daterange: "+word);
                    saveword=false;
                    lookingformonth=false;


                }
                if(wordafterdate && word.equals("-") ){
                    wordafterdate=false;
                    saveword=true;
                }

                if(!ne.equals("DATE")){
                    //txt=txt.replaceFirst("\\b"+java.util.regex.Pattern.quote(word)+"\\b"," ");//regexs are weird
                    //txt=txt.replaceFirst(java.util.regex.Pattern.quote(word)," ");//regexs are weird
                    wordafterdate=false;

                    System.out.println("replacing this word:"+word);
                    datestring.append(" ");

                }else {
                    if(!word.matches(".*[0-9].*") && lookingformonth){
                        monthsave=word;
                        System.out.println("month recognized: "+monthsave+rangedate);
                    }

                    datestring.append(" ").append(word);
                    wordafterdate=true;
                    saveword=false;


                }
            }
            //txt = txt.replaceAll("[()]", "");
            datestring= new StringBuilder((datestring.toString()));
        }
        datestring.append(monthsave).append(" ").append(rangedate);
        txt = txt.replaceAll("\\.", " ");
        datestring = new StringBuilder(replace(datestring.toString()));
        //end of the words trimming part
        System.out.println("datestring:"+datestring);
        //System.out.println("txt:"+txt);
        String keepyear=""; //this is a year for those odd situations where a year is mentioned earlier, but then later refernces to the date do not contain the year
        String keepmonth="";
        Boolean addedmonth=false,addedday=false;
        ArrayList <String>dates= new <String>ArrayList();
        Annotation document1 = new Annotation(datestring.toString());
        // run all Annotators on this text
        pipeline.annotate(document1);
        Integer rank=5;

        System.out.println(document1.get(CoreAnnotations.TextAnnotation.class));
        List<CoreMap> timexAnnsAll=document1.get(TimeAnnotations.TimexAnnotations.class);
        for(CoreMap cm:timexAnnsAll){
            List<CoreLabel> tokens=cm.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println(cm + " [from char offset " +
                    tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                    " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                    " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());

            timeref++;
            SUTime.Temporal tim=cm.get(TimeExpression.Annotation.class).getTemporal();
            String time=tim.toISOString();
            System.out.println("we get this string:"+time);
            rank=5;
            addedday=false;
            addedmonth=false;
            if (time!=null){
                if(!time.substring(0, 4).contains("X")){
                    keepyear=time.substring(0,4);
                    System.out.println("saved the year of "+keepyear);

                }
                if(hasmonth(time)){
                    keepmonth=time.substring(5,7);
                    System.out.println("saved month of "+keepmonth);

                }
                if(!hasyear(time)){
                    if(keepyear.equals("")){
                        System.out.println("searching whole sentence for year");
                        for(CoreMap c:timexAnnsAll){
                            SUTime.Temporal findyear=c.get(TimeExpression.Annotation.class).getTemporal();
                            String findyears=findyear.toISOString();
                            if(!findyears.substring(0, 4).contains("X")){
                                keepyear=findyears.substring(0,4);
                                System.out.println("saved the year of(findyears func) "+keepyear);
                                break;
                            }
                        }
                    }
                    time=time.substring(4);//cuts out year(XXXX) if there is none
                    time=keepyear+time;
                    System.out.println("using saved year");

                }

                if(!hasmonth(time)){
                    time=time+"-01";//adds a start month of january if we only get a year
                    addedmonth=true;
                }
                if(!hasday(time)){
                    time=time+"-01"; //adds a start day of 1 if we only get a month
                    addedday=true;
                }
                if(addedday){
                    rank=3;
                    System.out.println("Setting rank of 3");
                }
                if(addedmonth){
                    rank=2;
                }
                try{
                    LocalDate d=LocalDate.parse(time);
                    System.out.println(d);
                    LocalDateTime dt=d.atStartOfDay();
                    String begintime=dt.toString();
                    begintime=begintime+":00Z";
                    System.out.println(begintime);
                    dates.add(begintime);
                    dateranks.add(rank);


                }catch (Exception e){
                    System.out.println("got an exception of "+e);
                }
                System.out.println("removing this because already analyzed:"+cm);
                datestring = new StringBuilder(datestring.toString().replaceFirst("\\b" + cm + "\\b", " "));

            }

        }

        System.out.println(dateranks);
        System.out.println(dates);
        System.out.println("after everyting we got this:"+datestring);
        if(dates.size()>1){
            if(dates.get(0).equals(dates.get(1))){
                dates.remove(1);
                dateranks.remove(1);
            }
        }

        if(dates.size()>=2){
            System.out.println("has two dates");
        }else if(dates.size()==1){//if only one date or month is extracted, we make two dates/times
            if(addedmonth){
                String year= dates.get(0);
                dates.add(1,year.substring(0,4)+"-12-31T00:00:00Z");
                rank=2;
                dateranks.add(rank);
            }else if(addedday){
                String dateString = dates.get(0);
                dateString=dateString.substring(0,10);
                LocalDate date = LocalDate.parse(dateString);
                LocalDate newDate=null;
                if(date.getMonth().toString().equals("FEBRUARY")){//if month is february, dont use leap year days becuase it causes problems
                    System.out.println("no leap year lolll");
                    newDate = date.withDayOfMonth(date.getMonth().maxLength()-1);

                }else {
                    newDate = date.withDayOfMonth(date.getMonth().maxLength());//finds last day of the month to create a begin and end date

                }
                System.out.println("got one date, using last day of month");
                dates.add(1,newDate+"T00:00:00Z");//adding last date of month to arraylist
                rank=3;
                dateranks.add(rank);

            }
        }else {
            System.out.println("something went very wrong.");//program should never get here
        }
        ArrayList datesplusranks=new <String>ArrayList();
        for(int i=0;i<dates.size();i++ ){
            String[] arr = new String[2];
            arr[0]=dates.get(i);
            arr[1]= String.valueOf(dateranks.get(i));

            datesplusranks.add(i,arr);
            //datesplusranks.add(arr);
            System.out.println("adding "+dates.get(i)+" at index "+i);
            //String[]arrayc=new String[2];
            //arrayc= (String[]) datesplusranks.get(i);
            //System.out.println("rank adding "+ arrayc[1]);


        }
        //insertion sort
        for (int i=1; i<datesplusranks.size(); ++i)
        {
            int key = Integer.parseInt(((String[])datesplusranks.get(i))[1]);
            int j;
            for(j=i-1;j>=0 && key>Integer.parseInt(((String[])datesplusranks.get(j))[1]); j--){
                Collections.swap(datesplusranks,j+1,j);
            }
            System.out.println("index "+i+" is "+Integer.parseInt(((String[])datesplusranks.get(i))[1]));
        }
        int stopval=2;
        Integer keyval=findkeyval(datesplusranks);

        if(datesplusranks.size()>2){
            for(int i=1;i<datesplusranks.size();i++){

                if (Integer.parseInt(((String[])datesplusranks.get(i))[1])<keyval){
                    stopval=i-1;
                    break;
                }
            }
            for (int i=1; i<stopval; ++i) {
                int j;
                String[]arr=(String[])datesplusranks.get(i);
                LocalDate d1=LocalDate.parse(arr[0].substring(0,10));
                for(j=i-1;j>=0 && d1.isBefore(LocalDate.parse(((String[])datesplusranks.get(j))[0].substring(0,10))); j--){
                    Collections.swap(datesplusranks,j+1,j);
                }
                System.out.println("index "+i+" is "+Integer.parseInt(((String[])datesplusranks.get(i))[1]));
            }
            for (int x=1; x<4;  x++){
                try{
                    if((((String[])datesplusranks.get(0))[0]).equals((((String[])datesplusranks.get(1))[0]))){
                        datesplusranks.remove(1);
                        System.out.println("removing dupes");
                    }
                }catch (Exception e){
                    System.out.println("error of" + e);
                }
            }

        }

        // removes duplicates, just in case
        Set<String> hs = new LinkedHashSet<>();
        hs.addAll(datesplusranks);
        datesplusranks.clear();
        datesplusranks.addAll(hs);

        if (datesplusranks.size()<=0){
            System.out.println("no date found!");

        }else{
            if (datesplusranks.size()==1){
                datesplusranks.add(1,datesplusranks.get(0));

            }
            if(Integer.parseInt(((String[])datesplusranks.get(0))[1])<=3 && Integer.parseInt(((String[])datesplusranks.get(1))[1])==5)  {
                datesplusranks.add(0,datesplusranks.get(1));

            }
            if(Integer.parseInt(((String[])datesplusranks.get(1))[1])<=3 && Integer.parseInt(((String[])datesplusranks.get(0))[1])==5){
                datesplusranks.add(1,datesplusranks.get(0));

            }


            String[]arr=(String[])datesplusranks.get(0);
            LocalDate d1=LocalDate.parse(arr[0].substring(0,10));
            arr=(String[])datesplusranks.get(1);
            LocalDate d2=LocalDate.parse(arr[0].substring(0,10));
            //checks that dates are in right order after removing duplicates, which can mess up order
            if(d1.isAfter(d2)){
                Collections.swap(datesplusranks,1,0);
                System.out.println("swapping");
            }
            System.out.println("start date: "+ ((String[])datesplusranks.get(0))[0]);
            System.out.println("end date: "+((String[])datesplusranks.get(1))[0]);
            System.out.println("dateranks:"+((String[])datesplusranks.get(0))[1]);
            System.out.println("dateranks1:"+((String[])datesplusranks.get(1))[1]);

        }
        categories.add(category);
        timerefs.add(timeref);
        return datesplusranks;
    }
    private static String replace(String txt){
        txt = txt.replaceAll("[ ]{3,}", " sp ");

        txt=txt.replaceAll("current","");
        txt=txt.replaceAll("previously","");
        txt=txt.replaceAll("month","");
        txt=txt.replaceAll("quarter","");

        return txt;
    }
    private static Integer findkeyval(ArrayList datesplusranks){
        Integer key=0;
        for (Object datesplusrank : datesplusranks) {
            if (key < Integer.parseInt(((String[]) datesplusrank)[1])) {
                key = Integer.parseInt(((String[]) datesplusrank)[1]);
            }
        }
        return key;
    }
    //TODO figure out how to classify words.
    private static String classify(String txt){
        String cat="";
        if(txt.equals("eruption") || txt.equals("earthquake")){
            cat="event";
        }else if(txt.equalsIgnoreCase("survey") ||txt.equalsIgnoreCase("study") ||txt.equalsIgnoreCase("expedition")||txt.equalsIgnoreCase("research")||txt.equalsIgnoreCase("results")||txt.equalsIgnoreCase("summary")||txt.equalsIgnoreCase("investigations") ){
            cat="range";
        }else if(txt.equalsIgnoreCase("surveys")||txt.equalsIgnoreCase("conclusion") ||txt.equalsIgnoreCase("report")){
            cat="range";
        } else {
            cat=null;
        }


        return cat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TemporalNLP{");
       // sb.append("name='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println(System.getProperty("user.dir"));
        Properties props = new Properties();



        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,");
       //props.setProperty("ner.useSUTime", "0");
        StanfordCoreNLP coreNLP = new StanfordCoreNLP(props);
        coreNLP.addAnnotator(new TimeAnnotator("sutime",props));
        // coreNLP.addAnnotator(new TimeAnnotator());

        ArrayList dates =  TemporalNLP.getdates("january 2001  to  dec 2010", coreNLP);

    }
}