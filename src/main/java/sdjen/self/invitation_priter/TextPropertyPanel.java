package sdjen.self.invitation_priter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import sdjen.self.invitation_priter.ttf.TTF;

public class TextPropertyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public JTextArea selectTextComp;
	public JTextField xTextField;
	public JTextField yTextField;
	public JTextField wTextField;
	public JTextField hTextField;
	public JTextField sTextField;
	public JTextField cTextField;
	public JCheckBox boldCkbx;
	public JCheckBox italicCkbx;

	/**
	 * Create the panel.
	 */
	public TextPropertyPanel() {
		setLayout(new BorderLayout());
		{
			add(cTextField = createTextField(new FocusAdapter() {
				String t;

				@Override
				public void focusGained(FocusEvent e) {
					t = ((JTextField) e.getComponent()).getText();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (((JTextField) e.getComponent()).getText().equals(t))
						return;
					selectTextComp.setText(((JTextField) e.getComponent()).getText());
				}
			}), BorderLayout.NORTH);
		}
		{
			JPanel rectanglePanel = new JPanel(new BorderLayout());
			add(rectanglePanel, BorderLayout.WEST);
			rectanglePanel.setBorder(BorderFactory.createEmptyBorder(0, -5, 0, -5));
			rectanglePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
			FocusListener listener = new FocusAdapter() {
				String t;

				@Override
				public void focusGained(FocusEvent e) {
					t = ((JTextField) e.getComponent()).getText();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (((JTextField) e.getComponent()).getText().equals(t))
						return;
					selectTextComp.setBounds(toInt(xTextField.getText()), toInt(yTextField.getText()),
							toInt(wTextField.getText()), toInt(hTextField.getText()));
				}
			};
			rectanglePanel.add(createLabel(15, "X"));
			rectanglePanel.add(xTextField = createTextField(listener));
			rectanglePanel.add(createLabel(15, "Y"));
			rectanglePanel.add(yTextField = createTextField(listener));
			rectanglePanel.add(createLabel(15, "宽"));
			rectanglePanel.add(wTextField = createTextField(listener));
			rectanglePanel.add(createLabel(15, "高"));
			rectanglePanel.add(hTextField = createTextField(listener));
			rectanglePanel.setPreferredSize(new Dimension(60, 85));
		}
		{
			JPanel fontPanel = new JPanel(new BorderLayout());
			add(fontPanel, BorderLayout.EAST);
			fontPanel.setBorder(BorderFactory.createEmptyBorder(0, -5, 0, -5));
			fontPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
			fontPanel.add(createLabel(30, "字号"));
			fontPanel.add(sTextField = createTextField(new FocusAdapter() {
				String t;

				@Override
				public void focusGained(FocusEvent e) {
					t = ((JTextField) e.getComponent()).getText();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (((JTextField) e.getComponent()).getText().equals(t))
						return;
					resetFont();
				}
			}));
			fontPanel.add(boldCkbx = createCheckBox("粗体"));
			fontPanel.add(italicCkbx = createCheckBox("斜体"));
			fontPanel.setPreferredSize(new Dimension(70, 85));
		}
	}

	private void resetFont() {
		try {
			int stlye = boldCkbx.isSelected() ? Font.BOLD : Font.PLAIN;
			if (italicCkbx.isSelected())
				stlye = stlye | Font.ITALIC;
			selectTextComp.setFont(TTF.getFont(stlye, Float.valueOf(sTextField.getText())));
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(TextPropertyPanel.this, e1.getMessage());
		}
	}

	private JCheckBox createCheckBox(String text) {
		final JCheckBox result = new JCheckBox(text);
		result.setHorizontalTextPosition(JCheckBox.LEFT);
		result.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (null != selectTextComp && result.isFocusOwner())
					resetFont();
			}
		});
		return result;
	}

	private JLabel createLabel(int width, String text) {
		JLabel result = new JLabel(text);
		result.setPreferredSize(new Dimension(width, 15));
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		return result;
	}

	public void setSelectTextComp(JTextArea component) {
		this.selectTextComp = component;
		Rectangle rectangle = component.getBounds();
		cTextField.setText(component.getText());
		xTextField.setText(String.valueOf(rectangle.x));
		yTextField.setText(String.valueOf(rectangle.y));
		wTextField.setText(String.valueOf(rectangle.width));
		hTextField.setText(String.valueOf(rectangle.height));
		sTextField.setText(String.valueOf(component.getFont().getSize()));
		boldCkbx.setSelected(component.getFont().isBold());
		italicCkbx.setSelected(component.getFont().isItalic());
	}

	private int toInt(String t) {
		try {
			return Integer.valueOf(t);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private JTextField createTextField(FocusListener listener) {
		final JTextField result = new JTextField();
		result.setPreferredSize(new Dimension(35, 20));
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		// result.setFocusable(false);
		result.addFocusListener(listener);
		return result;
	}
}
