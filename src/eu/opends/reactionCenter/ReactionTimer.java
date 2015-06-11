/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.reactionCenter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.opends.jasperReport.ReactionLogger;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public abstract class ReactionTimer
{
	protected Simulator sim;
	
	protected ReactionLogger reactionLogger;
	protected long experimentStartTime;
	
	protected String timerID;
	protected int index;
	
	protected Calendar reactionTimer = null;
	protected String comment;
	protected String reactionGroupID;

	protected boolean correctReactionReported = false;
	protected boolean failureReactionReported = false;
	
	protected boolean timerIsActive = false;
	
	protected TrialLogger trialLogger;
	
	long reactionStartTime;

	long relativeStartTime;

	private int reactionResult;

	private long reactionTime;
	



	public ReactionTimer(Simulator sim, ReactionLogger reactionlogger, long experimentStartTime, String timerID, int index)
	{
		this.sim = sim;
		this.reactionLogger = reactionlogger;
		this.experimentStartTime = experimentStartTime;
		this.timerID = timerID;
		this.index = index;
	}

	
	public void setup(String newReactionGroupID, String newComment)
	{
		if(timerIsActive)
			reportMissedReaction();

		reactionGroupID = newReactionGroupID;
		comment = newComment;
		resetTimer();
		
		trialLogger = new TrialLogger(newReactionGroupID, comment);
		
		System.err.println("Setup reaction timer '" + timerID + "' (reaction group: '" + reactionGroupID + "')");
	}


	public void reportMissedReaction()
	{
		if(timerIsActive)
		{
			// report previous reaction as missing
			reactionStartTime = reactionTimer.getTimeInMillis();
			relativeStartTime = reactionStartTime - experimentStartTime;
			//Is used in test 3 when some stimulis are supposed to be missing
			if (comment.equalsIgnoreCase("ignore")) { 
				reactionResult = 1;
				reactionTime = 0L;
				reactionLogger.add(this);
			} else {
				reactionResult = -2;
				reactionTime = 10000L;
				reactionLogger.add(this);
			}
			trialLogger.setReaction(0);
			//trialLogger.writeLog();
			
			reactionTimer = null;
			timerIsActive = false;
		}
	}

	public String getReactionGroupID() {
		return reactionGroupID;
	}
	

	public long getReactionStartTime() {
		return reactionStartTime;
	}


	public long getRelativeStartTime() {
		return relativeStartTime;
	}
	
	public int getReactionResult() {
		return reactionResult;
	}
	

	public long getReactionTime() {
		return reactionTime;
	}

	
	public void update()
	{
		if(timerIsActive)
		{
			reactionStartTime = reactionTimer.getTimeInMillis();
			relativeStartTime = reactionStartTime - experimentStartTime;
			long currentTime = new GregorianCalendar().getTimeInMillis();
			reactionTime = currentTime - reactionStartTime;
			
			if(correctReactionReported)
			{		
				// report correct reaction
				this.reactionResult = 1;
				reactionLogger.add(this);

				reactionTimer = null;
				comment = "";
				
				System.err.println("Correct: " + reactionTime);
				timerIsActive = false;
			}
			else if(failureReactionReported)
			{
				// report failure reaction
				this.reactionResult = -1;
				reactionLogger.add(this);
				
				reactionTimer = null;
				comment = "";
				
				System.err.println("Failure: " + reactionTime);
				timerIsActive = false;
			}
		}
	}
	
	
	public void reportCorrectReaction()
	{
		correctReactionReported = true;
	}
	
	
	public void reportFailureReaction()
	{
		failureReactionReported = true;
	}
	
	
	public void close()
	{
		if(timerIsActive)
			reportMissedReaction();
	}


	public String getTimerID() 
	{
		return timerID;
	}
	
	
	public int getIndex() 
	{
		return index;
	}
	
	
	private void resetTimer()
	{
		reactionTimer = new GregorianCalendar();
		correctReactionReported = false;
		failureReactionReported = false;
	}


	public String getComment() {
		return comment;
	}
}
