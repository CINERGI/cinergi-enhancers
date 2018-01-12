package org.neuinfo.foundry.enhancers.common;


//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;



import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neuinfo.foundry.enhancers.plugins.TemporalEnhancer;

import java.util.List;

public class TemporalTest {
    private  static Temporal t;

    private final static Logger logger = Logger.getLogger(TemporalEnhancer.class);

    @BeforeClass
    public static void initialize() throws Exception {
        t = new Temporal();
    }
    @Test
    public void testTemporal1(){

        List<CoreMap> timexAnnsAll =  t.getdates("January 2010");

        Assert.assertEquals(1,timexAnnsAll.size());

        CoreMap cm = timexAnnsAll.get(0);
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        Assert.assertEquals("2010-01",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());
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

        List<CoreMap> timexAnnsAll =  t.getdates("2010-10-01");
//        Assert.assertTrue(tokens.get(TimeExpression.Annotation.class).getTemporal());
//          tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
//                " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
//                " --> " + tokens.get(TimeExpression.Annotation.class).getTemporal());
        Assert.assertEquals(1,timexAnnsAll.size());

        CoreMap cm = timexAnnsAll.get(0);
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        Assert.assertEquals("2010-10-01",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());

    }
    @Test
    public void testTemporal3(){

        List<CoreMap> timexAnnsAll =  t.getdates("January 2010 to December 2012");

        Assert.assertEquals(1,timexAnnsAll.size());

        CoreMap cm = timexAnnsAll.get(0);
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2010-01/2012-12",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());

    }
    @Test
    public void testTemporal4(){

        List<CoreMap> timexAnnsAll =  t.getdates("from  January 2010 to December 2012");

        Assert.assertEquals(1,timexAnnsAll.size());

        CoreMap cm = timexAnnsAll.get(0);
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2010-01/2012-12",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());



    }
    @Test
    public void testTemporal5(){

        List<CoreMap> timexAnnsAll =  t.getdates("between January 2010 and December 2012");

        Assert.assertEquals(2,timexAnnsAll.size());

        CoreMap cm = timexAnnsAll.get(0);
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        String iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2010-01",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());

         cm = timexAnnsAll.get(1);
         tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
         iso = cm.get(TimeExpression.Annotation.class).getTemporal().toISOString();

        Assert.assertEquals("2012-12",cm.get(TimeExpression.Annotation.class).getTemporal().toISOString());

    }
}

