/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.display;

import com.pixelmed.display.event.ApplyICCProfileChangeEvent; 
import com.pixelmed.event.ApplicationEventDispatcher; 
import com.pixelmed.event.EventContext;

import java.awt.*; 
import java.awt.event.*; 
import java.awt.image.*; 
import javax.swing.*; 
import javax.swing.event.*;

import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

/**
 * @author	dclunie
 */
class SourceImageApplyICCProfileSelectorPanel extends JPanel {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/display/SourceImageApplyICCProfileSelectorPanel.java,v 1.2 2026/03/08 15:20:37 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(SourceImageApplyICCProfileSelectorPanel.class);

	/***/
	private EventContext eventContext;
	/***/
	private ButtonGroup applyICCProfileButtons;
	/***/
	private JRadioButton appliedButton;
	/***/
	private JRadioButton notAppliedButton;
	
	/***/
	private class SourceImageApplyICCProfileChangeActionListener implements ActionListener {

		/**
		 */
		public SourceImageApplyICCProfileChangeActionListener() {
		}
		/**
		 * @param	event
		 */
		public void actionPerformed(ActionEvent event) {
//System.err.println("SourceImageApplyICCProfileChangeActionListener.SourceImageApplyICCProfileChangeActionListener.actionPerformed()");
			sendEventCorrespondingToCurrentButtonState();
		}
	}
	
	public void sendEventCorrespondingToCurrentButtonState() {
		String choice = applyICCProfileButtons.getSelection().getActionCommand();
		try {
			ApplicationEventDispatcher.getApplicationEventDispatcher().processEvent(
				new ApplyICCProfileChangeEvent(eventContext,choice));
		} catch (Exception e) {
			slf4jlogger.error("",e);
		}
	}
	
	/**
	 * @param	eventContext
	 */
	public SourceImageApplyICCProfileSelectorPanel(EventContext eventContext) {
		this.eventContext=eventContext;
		
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		JPanel applyICCProfileControlsPanel = new JPanel();
		add(applyICCProfileControlsPanel);

		applyICCProfileControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		applyICCProfileControlsPanel.add(new JLabel("ICC Profile"));

		applyICCProfileButtons = new ButtonGroup();
		SourceImageApplyICCProfileChangeActionListener listener = new SourceImageApplyICCProfileChangeActionListener();

		appliedButton = new JRadioButton("Applied",true);
		appliedButton.setActionCommand(ApplyICCProfileChangeEvent.iccProfileApplied);
		appliedButton.setToolTipText("Apply ICC Profile if present");
		appliedButton.addActionListener(listener);
		applyICCProfileButtons.add(appliedButton);
		applyICCProfileControlsPanel.add(appliedButton);

		notAppliedButton = new JRadioButton("Not Applied",false);
		notAppliedButton.setActionCommand(ApplyICCProfileChangeEvent.iccProfileNotApplied);
		notAppliedButton.setToolTipText("Do not apply ICC Profile");
		notAppliedButton.addActionListener(listener);
		applyICCProfileButtons.add(notAppliedButton);
		applyICCProfileControlsPanel.add(notAppliedButton);
	}
}

