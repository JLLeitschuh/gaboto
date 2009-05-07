/**
 * Copyright 2009 University of Oxford
 *
 * Written by Arno Mittelbach for the Erewhon Project
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  - Neither the name of the University of Oxford nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.oucs.gaboto.timedim;

import java.util.Calendar;

/**
 * A Java Bean to work with time instants.
 * 
 * @author Arno Mittelbach
 * @version 0.1
 */
public class TimeInstant extends TimeSpan implements Comparable<TimeInstant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1460039890377361871L;

	/**
	 * Create an unspecified time instant.
	 */
	public TimeInstant(){}
	
	/**
	 * Create a fully specified time instant.
	 * 
	 * @param year The year.
	 * @param month The month.
	 * @param day The day.
	 */
	public TimeInstant(Integer year, Integer month, Integer day){
		setStartYear(year);
		setStartMonth(month);
		setStartDay(day);
	}
	
	/**
	 * Creates a time instant representing now.
	 * 
	 * @return A time instant representing now.
	 */
	public static TimeInstant now(){
		Calendar currentDate = Calendar.getInstance();
		return new TimeInstant(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
	}
	
	@Override
	public Integer getDurationDay() {
		return 0;
	}

	@Override
	public void setDurationDay(Integer durationDay) {
		throw new IllegalStateException("Time instants do not have a duration.");
	}

	@Override
	public Integer getDurationMonth() {
		return 0;
	}

	@Override
	public void setDurationMonth(Integer durationMonth) {
		throw new IllegalStateException("Time instants do not have a duration.");
	}

	@Override
	public Integer getDurationYear() {
		return 0;
	}

	@Override
	public void setDurationYear(Integer durationYear) {
		throw new IllegalStateException("Time instants do not have a duration.");
	}

	@Override
	public boolean hasFixedDuration(){
		throw new IllegalStateException("Time instants do not have a duration.");
	}
	
	@Override
	public boolean contains(TimeSpan ts){
		if(ts instanceof TimeInstant){
			return ts.equals(this);
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof TimeInstant))
			return false;
		
		TimeInstant ti = (TimeInstant) obj;
		
		return 	( this.getStartYear() == ti.getStartYear() || (null != this.getStartYear() && this.getStartYear().equals(ti.getStartYear()))) && 
				( this.getStartMonth() == ti.getStartMonth() || (null != this.getStartMonth() && this.getStartMonth().equals(ti.getStartMonth()))) && 
				( this.getStartDay() == ti.getStartDay() || (null != this.getStartDay() && this.getStartDay().equals(ti.getStartDay())));
	}

	@Override
	public String toString(){
		if(this.equals(TimeUtils.BIG_BANG))
			return "big-bang";
		if(this.equals(TimeUtils.DOOMS_DAY))
			return "dooms-day";
		
		String s = "";
		
		s += startYear;
		if(null != startMonth)
			s += "-" + startMonth;
		if(null != startDay)
			s += "-" + startDay;
		
		return s;
	}
	
	/**
	 * Tests if to instants are roughly the same .. for example 300-3-18 is roughly the same as 300-null-null
	 * 
	 * @param ti The time instant to test this instant against.
	 * 
	 * @return True, if they are roughly the same.
	 */
	public boolean aboutTheSame(TimeInstant ti){
		if(this.startYear.equals(ti.getStartYear()) ) {
			// years are the same
			if(null == this.getStartMonth() || null == ti.getStartMonth())
				return true;
			else if(this.getStartMonth().equals(ti.getStartMonth())) {
				// months are the same
				if(null == this.getStartDay() || null == ti.getStartDay())
					return true;
				else if(this.getStartDay().equals(ti.getStartDay()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Compares this time instant to another.
	 * 
	 * @param ti The other time instant.
	 * 
	 * @return -1, if this instant is earlier. 1, if it is later. 0 else.
	 */
	public int compareTo(TimeInstant ti) {
		if(this.startYear < ti.getStartYear())
			return -1;
		else if(this.startYear > ti.getStartYear())
			return 1;
		else {
			// years are the same
			
			if(null == this.getStartMonth() && null == ti.getStartMonth())
				return 0;
			// we define that if one is null then both are more or less equal
			else if(null == this.getStartMonth())
				return 0;
			else if(null == ti.getStartMonth())
				return 0;
			else if(this.getStartMonth() < ti.getStartMonth())
				return -1;
			else if(this.getStartMonth() > ti.getStartMonth())
				return 1;
			else {
				// months are the same
				
				if(null == this.getStartDay() && null == ti.getStartDay())
					return 0;
				// we define that if one is null then both are more or less equal
				else if(null == this.getStartDay())
					return 0;
				else if(null == ti.getStartDay())
					return 0;
				else if(this.getStartDay() < ti.getStartDay())
					return -1;
				else if(this.getStartDay() > ti.getStartDay())
					return 1;
			}
		}
		// they seem to be equal
		return 0;
	}

}