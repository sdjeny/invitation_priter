package sdjen.self.invitation_priter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
	public Text selectTextComp;
	public JTextField xTextField;
	public JTextField yTextField;
	public JTextField wTextField;
	public JTextField hTextField;
	public JTextField sTextField;
	public JTextField cTextField;
	public JCheckBox boldCkbx;
	public JCheckBox italicCkbx;
	public JComboBox fontComboBox;

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
					selectTextComp.setName(((JTextField) e.getComponent()).getText());
				}
			}), BorderLayout.NORTH);
		}
		{
			JPanel rectanglePanel = new JPanel(new BorderLayout());
			add(rectanglePanel, BorderLayout.CENTER);
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
		}
		{
			JPanel fontPanel = new JPanel(new BorderLayout());
			add(fontPanel, BorderLayout.SOUTH);
			// fontPanel.setBorder(BorderFactory.createEmptyBorder(0, -5, 0,
			// -5));
			{
				fontPanel.add(
						fontComboBox = new JComboBox(
								GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()),
						BorderLayout.CENTER);
//				fontComboBox.setPreferredSize(new Dimension(160, 22));
				fontComboBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED)
							resetFont();
					}
				});
			}
			{
				JPanel fontDetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
				fontDetPanel.setBorder(BorderFactory.createEmptyBorder(0, -5, 0, -5));
				fontPanel.add(fontDetPanel, BorderLayout.SOUTH);
				fontDetPanel.add(createLabel(30, "字号"));
				fontDetPanel.add(sTextField = createTextField(new FocusAdapter() {
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
				fontDetPanel.add(boldCkbx = createCheckBox("粗体"));
				fontDetPanel.add(italicCkbx = createCheckBox("斜体"));
			}
			 fontPanel.setPreferredSize(new Dimension(0, 50));
		}
		// setPreferredSize(new Dimension(160, 150));
	}

	private void resetFont() {
		try {
			Font font = getFont();
			Double size = Double.valueOf(sTextField.getText());
			int style = boldCkbx.isSelected() ? Font.BOLD : Font.PLAIN;
			if (italicCkbx.isSelected())
				style = style | Font.ITALIC;
			String fontName = (String) fontComboBox.getSelectedItem();
			if (0 != font.getSize2D() - size || 0 != style - font.getStyle() || !fontName.equals(font.getFontName()))
				selectTextComp.setFont(new Font(fontName, style, size.intValue()));
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

	public void setSelectTextComp(Text component) {
		this.selectTextComp = component;
		Rectangle rectangle = component.getBounds();
		cTextField.setText(component.getName());
		xTextField.setText(String.valueOf(rectangle.x));
		yTextField.setText(String.valueOf(rectangle.y));
		wTextField.setText(String.valueOf(rectangle.width));
		hTextField.setText(String.valueOf(rectangle.height));
		Font font = component.getFont();
		sTextField.setText(String.valueOf(font.getSize()));
		boldCkbx.setSelected(font.isBold());
		italicCkbx.setSelected(font.isItalic());
		fontComboBox.setSelectedItem(font.getFontName());
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
		result.addFocusListener(listener);
		return result;
	}
}
