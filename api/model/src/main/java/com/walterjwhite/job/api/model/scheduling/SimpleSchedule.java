package com.walterjwhite.job.api.model.scheduling;

import com.walterjwhite.job.api.enumeration.Recurrence;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SimpleSchedule extends AbstractSchedule {
  @Column(updatable = false)
  protected int hourOfDay = -1;

  @Column(updatable = false)
  protected int minuteOfHour = -1;

  @Column(updatable = false)
  protected int dayOfMonth;

  @Column(updatable = false)
  protected int monthOfYear;

  @Column(updatable = false)
  protected Recurrence recurrence = Recurrence.NONE;

  @Column(updatable = false)
  protected long recurrenceAmount = 1;

  @Column(updatable = false)
  protected boolean runNowIfMissedFirstExecution = true;

  public int getHourOfDay() {
    return hourOfDay;
  }

  public void setHourOfDay(int hourOfDay) {
    this.hourOfDay = hourOfDay;
  }

  public int getMinuteOfHour() {
    return minuteOfHour;
  }

  public void setMinuteOfHour(int minuteOfHour) {
    this.minuteOfHour = minuteOfHour;
  }

  public int getDayOfMonth() {
    return dayOfMonth;
  }

  public void setDayOfMonth(int dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
  }

  public int getMonthOfYear() {
    return monthOfYear;
  }

  public void setMonthOfYear(int monthOfYear) {
    this.monthOfYear = monthOfYear;
  }

  public Recurrence getRecurrence() {
    return recurrence;
  }

  public void setRecurrence(Recurrence recurrence) {
    this.recurrence = recurrence;
  }

  public long getRecurrenceAmount() {
    return recurrenceAmount;
  }

  public void setRecurrenceAmount(long recurrenceAmount) {
    this.recurrenceAmount = recurrenceAmount;
  }

  public boolean isRunNowIfMissedFirstExecution() {
    return runNowIfMissedFirstExecution;
  }

  public void setRunNowIfMissedFirstExecution(boolean runNowIfMissedFirstExecution) {
    this.runNowIfMissedFirstExecution = runNowIfMissedFirstExecution;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SimpleSchedule that = (SimpleSchedule) o;
    return hourOfDay == that.hourOfDay
        && minuteOfHour == that.minuteOfHour
        && dayOfMonth == that.dayOfMonth
        && monthOfYear == that.monthOfYear
        && recurrenceAmount == that.recurrenceAmount
        && recurrence == that.recurrence
        && runNowIfMissedFirstExecution == that.runNowIfMissedFirstExecution;
  }

  @Override
  public int hashCode() {

    return Objects.hash(
        super.hashCode(),
        hourOfDay,
        minuteOfHour,
        dayOfMonth,
        monthOfYear,
        recurrence,
        recurrenceAmount,
        runNowIfMissedFirstExecution);
  }
}
