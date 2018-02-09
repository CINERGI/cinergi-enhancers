package org.neuinfo.foundry.enhancers.common;


//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;



import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neuinfo.foundry.enhancers.plugins.TemporalEnhancer;

import java.util.List;

public class TemporalNLPTest {
    private  static TemporalNLP t;

    private final static Logger logger = Logger.getLogger(TemporalEnhancer.class);

    @BeforeClass
    public static void initialize() throws Exception {
        t = new TemporalNLP();
    }
    @Test
    public void testTemporal1(){

        List<Temporal> timexAnnsAll =  t.getDates("January 2010");

        Assert.assertEquals(1,timexAnnsAll.size());

        //CoreMap cm = timexAnnsAll.get(0);
       // List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        //Assert.assertEquals("2010-01",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());
        Assert.assertEquals("January 2010",timexAnnsAll.get(0).getName());
        Assert.assertEquals("2010-01",timexAnnsAll.get(0).getIsoString());
        DateTime dt = new DateTime("2010-01T00:00:00Z").toDateTimeISO();

        Assert.assertEquals("2010-01",timexAnnsAll.get(0).getStartDate());
        Assert.assertNull(timexAnnsAll.get(0).getEndDate());
        Assert.assertEquals(timexAnnsAll.get(0).getDateType(),dateType.TimeInstant  );
//        for (CoreMap cm : timexAnnsAll) {
//            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
//            System.out.println(cm + " [from char offset " +
//                    tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
//                    " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
//                    " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
//        }
    }

    @Test
    public void testTemporal2(){

        List<Temporal> timexAnnsAll =  t.getDates("2010-10-01");
//        Assert.assertTrue(tokens.get(TimeExpression.Annotation.class).getTemporal());
//          tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
//                " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
//                " --> " + tokens.get(TimeExpression.Annotation.class).getTemporal());
        Assert.assertEquals(1,timexAnnsAll.size());

       // CoreMap cm = timexAnnsAll.get(0);
        //List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        Assert.assertEquals("2010-10-01",timexAnnsAll.get(0).getName());

    }
    @Test
    public void testTemporal3(){

        List<Temporal> timexAnnsAll =  t.getDates("January 2010 to December 2012");

        Assert.assertEquals(1,timexAnnsAll.size());

        //CoreMap cm = timexAnnsAll.get(0);
       // List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        //String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("January 2010 to December 2012",timexAnnsAll.get(0).getName());

    }
    @Test
    public void testTemporal4(){

        List<Temporal> timexAnnsAll =  t.getDates("from  January 2010 to December 2012");

        Assert.assertEquals(1,timexAnnsAll.size());

       // CoreMap cm = timexAnnsAll.get(0);
        //List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
       // String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("from January 2010 to December 2012",timexAnnsAll.get(0).getName());



    }
    @Test
    public void testTemporal5(){

        List<Temporal> timexAnnsAll =  t.getDates("\"Seismic Shot Point Navigation Data from the Long Island Shelf acquired during the R/V Endeavor expedition EN370 (2002)");

        Assert.assertEquals(1,timexAnnsAll.size());

       // CoreMap cm = timexAnnsAll.get(0);
        //List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
       // String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2002",timexAnnsAll.get(0).getName());



    }

    @Test
    public void testTemporal6(){

        List<Temporal> timexAnnsAll =  t.getDates("Well survey data from 2013-01-29T-00:31:00");

        Assert.assertEquals(1,timexAnnsAll.size());

        //CoreMap cm = timexAnnsAll.get(0);
       // List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
       // String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2013-07-14T00:31:00",timexAnnsAll.get(0).getName());

    }
}

