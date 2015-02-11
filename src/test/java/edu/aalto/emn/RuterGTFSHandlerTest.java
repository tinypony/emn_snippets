package edu.aalto.emn;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.aalto.emn.dataobject.ScheduleStop;

public class RuterGTFSHandlerTest {

	ScheduleStop stop;
	
	@Before
	public void setUp() {
		stop = new ScheduleStop();
	}
	
	@Test
	public void testCorrectTimeReturned() {
		assertEquals("Time is not correct", "0050", stop.getTime("HHmm", 3000));
	}
}
