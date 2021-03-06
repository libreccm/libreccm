/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Duration {
    
    private long days;
    
    private long hours;
    
    private long minutes;

    public static Duration fromMinutes(final long value) {
        final long days = value / (24 * 60);
        final long daysReminder = value % (24 * 60);
        final long hours = daysReminder / 60;
        final long minutes = daysReminder % 60;
        
        final Duration result = new Duration();
        result.setDays(days);
        result.setHours(hours);
        result.setMinutes(minutes);
        return result;
    }
    
    public long getDays() {
        return days;
    }

    public void setDays(final long days) {
        this.days = days;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(final long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(final long minutes) {
        this.minutes = minutes;
    }
    
    public long toMinutes() {
        return days * 24 * 60 + hours * 60 + minutes;
    }
    
    
}
