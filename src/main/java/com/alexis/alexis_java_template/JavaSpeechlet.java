package com.alexis.alexis_java_template;

import com.alexis.alexis_java_template.Traffic.TrafficReport;
import com.alexis.alexis_java_template.Traffic.TrafficReportHandler;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import java.io.IOException;

public class JavaSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(JavaSpeechlet.class);
    private TrafficReportHandler trh = new TrafficReportHandler();
    
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = intent.getName();

        if ("handleEventRequest".equals(intentName)) {
            return handleEventRequest(intent, session);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            // Create the plain text output.
            String speechOutput
                    = "Welcome to the Highways agencies accident update service. I can help tell you about the accidents in your country."
                    + "For example saying, Alexa tell me about accidents in Kent, will tell you about accidents in Kent";

            String repromptText = "Which county would you like to know more about?";
            
            return newAskResponse(speechOutput, false, repromptText, false);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // any session cleanup logic would go here
    }

    /**
     * Function to handle the onLaunch skill behaviour.
     *
     * @return SpeechletResponse object with voice/card response to return to
     * the user
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechOutput = "Introduction";
        // If the user either does not reply to the welcome message or says something that is not
        // understood, they will be prompted again with this text.
        String repromptText
                = "Long into and guide";

        return newAskResponse(speechOutput, false, repromptText, false);
    }

    /**
     * Wrapper for creating the Ask response from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param isOutputSsml whether the output text is of type SSML
     * @param repromptText the reprompt for if the user doesn't reply or is
     * misunderstood.
     * @param isRepromptSsml whether the reprompt text is of type SSML
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
            String repromptText, boolean isRepromptSsml) {
        OutputSpeech outputSpeech, repromptOutputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }

        if (isRepromptSsml) {
            repromptOutputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
        } else {
            repromptOutputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
        }
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }

    private SpeechletResponse handleEventRequest(Intent intent, Session session) {
        //TODO handle initial logic here
        try {
            trh.updateTrafficReports();
        } catch (Exception ex) {
            System.out.println("ERROR : " + ex.getStackTrace());
        }
        String speechOutput = "";
        
        if (trh.getNumberOfIncidents() == 0) {
            speechOutput
                    = "There are currently no accidents recorded. "
                    + "This could be due to a network issue with connecting to the Highway agency. "
                    + "Please try again in a minute";

            // Create the plain text output
            SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>" + speechOutput + "</speak>");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            StringBuilder speechOutputBuilder = new StringBuilder();
            StringBuilder cardOutputBuilder = new StringBuilder();

            //TODO Logic goes here
            ArrayList<TrafficReport> reports = trh.getCountyReportsFor(intent.getSlot("project").getValue());
            for (TrafficReport report : reports) {
                speechOutputBuilder.append("<p>").append(report.reportNaturally()).append("</p>");
                cardOutputBuilder.append("<p>").append(report.reportNaturally()).append("</p>");
            }
            String cardTitle = "Card";
            
            speechOutputBuilder.append(" Wanna go deeper in history?");
            cardOutputBuilder.append(" Wanna go deeper in history?");
            speechOutput = speechOutputBuilder.toString();

            String repromptText
                    = "Which county do you want a traffic update on?";

            // Create the Simple card content.
            SimpleCard card = new SimpleCard();
            card.setTitle(cardTitle);
            card.setContent(cardOutputBuilder.toString());

            SpeechletResponse response = newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false);
            response.setCard(card);
            return response;
        }
    }
}
