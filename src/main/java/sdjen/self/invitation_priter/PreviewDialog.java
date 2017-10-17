package sdjen.self.invitation_priter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PreviewDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private Component viewComp;

	public PreviewDialog(final InvitationPriterMain owner) {
		super(owner);
		setModal(true);
		final int w = (int) (owner.toDouble(owner.wTextField.getText()) + owner.toDouble(owner.xTextField.getText()));
		final int h = (int) (owner.toDouble(owner.hTextField.getText()) + owner.toDouble(owner.yTextField.getText()));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(viewComp = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				double xoff;
				double yoff;
				double scale;
				double sx = getWidth() - 1;
				double sy = getHeight() - 1;
				if (w / h < sx / sy) {
					scale = sy / h;
					xoff = 0.5 * (sx - scale * w);
					yoff = 0;
				} else {
					scale = sx / w;
					xoff = 0;
					yoff = 0.5 * (sy - scale * h);
				}
				g2.translate((float) xoff, (float) yoff);
				g2.scale((float) scale, (float) scale);
				Rectangle2D page = new Rectangle2D.Double(0, 0, w, h);
				g2.setPaint(Color.white);
				g2.fill(page);
				g2.setPaint(Color.black);
				g2.draw(page);
				try {
					owner.print(g2, null, 0);
				} catch (PrinterException e) {
					e.printStackTrace();
				}
			}
		}, BorderLayout.CENTER);
		viewComp.setPreferredSize(new Dimension(w, h));
		setSize(800, 600);
		setLocation((int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth()) / 2),
				(int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		viewComp.repaint();
	}
}
