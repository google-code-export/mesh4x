package com.swtdesigner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

import org.mesh4j.ektoo.ui.image.ImageManager;

/**
 * Custom implementation of a JToolTip
 * 
 * -- Ability to display a leading icon (icon to the left of the text)
 * 
 * ---------------------------------------------------- --- Future Enhancements
 * (in no particular order) ---
 * ----------------------------------------------------
 * 
 * <1> Allow HTML Text (Will open the doors for multiline tooltips with lines
 * determined by the <br>
 * syntax) <2> Balloon Shape (Change the shape to a balloon style, similiar to
 * xp) <3> Animated Text (An option to enable to text to appear to be written as
 * the user is hovering) <4> Title Section (Will require moving the icon into a
 * title area and the title appearing in bold, than displaying the text below
 * the title in plain)
 * 
 * ----------------------------------------------------
 * ----------------------------------------------------
 * ----------------------------------------------------
 * 
 * @since Jun 20, 2006 11:57:47 PM
 */
public final class CToolTip extends JToolTip {
	
	/**
	 * <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -428490306576408190L;

	/**
	 * Creates a Tooltip with an Icon
	 * 
	 * @param icon
	 *            The Icon to display (NULL Allowed)
	 */
	public CToolTip(final ImageIcon icon) {
		setUI(new CToolTipUI(icon));
	}

	/**
	 * Creates a Tooltip with no icon
	 */
	public CToolTip() {
		this(null);
	}

	/**
	 * Custom Implementation of MetalToolTipUI
	 * 
	 * @since Jun 21, 2006 8:25:40 AM
	 */
	private final class CToolTipUI extends MetalToolTipUI {
		private Image tooltipIcon = null;

		/**
		 * Default Constructor
		 * 
		 * @param tooltipIcon
		 *            The Icon to display or NULL if there is no icon to display
		 */
		public CToolTipUI(final ImageIcon tooltipIcon) {
			if (tooltipIcon != null) {
				this.tooltipIcon = tooltipIcon.getImage();
			}
		}

		/**
		 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
		 *      javax.swing.JComponent)
		 */
		public void paint(final Graphics g, final JComponent c) {
			String tipText = ((JToolTip) c).getTipText();

			if (tipText == null) {
				tipText = "";
			}

			g.setColor(c.getForeground());

			if (tooltipIcon != null) {
				g.drawImage(tooltipIcon, 3, 3, c);
				g.drawString(tipText, tooltipIcon.getWidth(c) + 6, 15);
			} else {
				g.drawString(tipText, 6, 15);
			}
		}

		/**
		 * @see javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
		 */
		public Dimension getPreferredSize(final JComponent c) {
			final FontMetrics metrics = c.getFontMetrics(c.getFont());
			String tipText = ((JToolTip) c).getTipText();

			if (tipText == null) {
				tipText = "";
			}

			final int width = 10
					+ SwingUtilities.computeStringWidth(metrics, tipText)
					+ (tooltipIcon == null ? 0 : tooltipIcon.getWidth(c));

			final int height = 6 + Math.max(metrics.getHeight(),
					tooltipIcon == null ? 0 : tooltipIcon.getHeight(c));

			return new Dimension(width, height);
		}
	}

	public static void main(String[] args) {
		// create a frame, add a label, and set the tooltip of the label to use
		// a test tooltip...
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Test");
				frame.getContentPane().setLayout(new BorderLayout());
				JLabel testLabel = new JLabel("This is a label with a tooltip") {
					private static final long serialVersionUID = -5729670732962721257L;

					public JToolTip createToolTip() {
						CToolTip tip = null;
						try {
							tip = new CToolTip(ImageManager
									.getSyncProcessIcon());
						}

						catch (Exception ex) {
							ex.printStackTrace();
						}
						tip.setComponent(this);
						return tip;
					}
				};
				testLabel
						.setToolTipText("Hello!  <b>I am a tooltip text string</b> \n This is another test");

				frame.getContentPane().add(testLabel, BorderLayout.CENTER);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}