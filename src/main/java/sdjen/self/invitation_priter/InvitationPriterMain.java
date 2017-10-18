package sdjen.self.invitation_priter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import com.fasterxml.jackson.databind.ObjectMapper;

import jxl.Sheet;
import jxl.Workbook;
import sdjen.self.invitation_priter.ttf.TTF;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InvitationPriterMain extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private TextPropertyPanel propertyPanel;
	private JButton printButton;
	private JPanel printPanel;
	private JTable textTable;
	private JTable loopTable;
	public JTextField xTextField;
	public JTextField yTextField;
	public JTextField wTextField;
	public JTextField hTextField;
	public JTextField dTextField;
	private String jsonFileName = "property.json";
	private String dataFileName = "data.xls";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InvitationPriterMain frame = new InvitationPriterMain();
					frame.loadData();
					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws Exception
	 */
	public InvitationPriterMain() throws Exception {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				for (Component comp : printPanel.getComponents()) {
					comp.requestFocus();
				}
			}
		});
		try {
			setIconImage(new ImageIcon(
					getClass().getClassLoader().getResource("sdjen/self/invitation_priter/invitation.png")).getImage());
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
			} catch (Exception e) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e2) {
		}
		setTitle("套打");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		{
			contentPane.add(getPrintPanel(), BorderLayout.CENTER);
		}
		{
			JScrollPane scrollPane = new JScrollPane(loopTable = new JTable(new DefaultTableModel()));
			loopTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					loopTableValueChanged(e);
				}
			});
			scrollPane.setPreferredSize(new Dimension(150, 0));
			contentPane.add(scrollPane, BorderLayout.EAST);
		}
		{
			JPanel panel = new JPanel();
			contentPane.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JPanel epanel = new JPanel();
				epanel.setLayout(new BorderLayout(0, 0));
				panel.add(epanel, BorderLayout.EAST);
				{
					epanel.add(propertyPanel = new TextPropertyPanel(), BorderLayout.CENTER);
					propertyPanel.setBorder(BorderFactory.createTitledBorder("内容属性"));
				}
				{
					JPanel espanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					epanel.add(espanel, BorderLayout.EAST);
					espanel.setBorder(BorderFactory.createTitledBorder("页面设置"));
					espanel.setPreferredSize(new Dimension(139, 0));
					espanel.add(createLabel(15, "X"));
					espanel.add(xTextField = createTextField());
					espanel.add(createLabel(15, "Y"));
					espanel.add(yTextField = createTextField());
					espanel.add(createLabel(15, "宽"));
					espanel.add(wTextField = createTextField());
					espanel.add(createLabel(15, "高"));
					espanel.add(hTextField = createTextField());
					espanel.add(new JLabel("旋转"));
					espanel.add(dTextField = createTextField());
					espanel.add(createViewButton());
					espanel.add(getPrintButton());
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane(getTextTable());
				scrollPane.setPreferredSize(new Dimension(0, 0));
				panel.add(scrollPane, BorderLayout.CENTER);
			}
		}
		try {
			initJson();
		} catch (Exception e) {
			e.printStackTrace();// TODO Auto-generated catch block
		}
	}

	private void loopTableValueChanged(ListSelectionEvent e) {
		int row = loopTable.getSelectedRow();
		DefaultTableModel model = (DefaultTableModel) loopTable.getModel();
		for (Component comp : printPanel.getComponents()) {
			if (comp instanceof Text) {
				Text textComp = (Text) comp;
				String texts = comp.getName();
				for (int col = 0; col < model.getColumnCount(); col++)
					texts = texts.replace(model.getColumnName(col), (String) loopTable.getValueAt(row, col));
				if (!comp.getName().equals(texts))
					textComp.setText(texts);
			}
		}
	}

	private JLabel createLabel(int width, String text) {
		JLabel result = new JLabel(text);
		result.setPreferredSize(new Dimension(width, 15));
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		return result;
	}

	private JTextField createTextField() {
		JTextField result = new JTextField("0");
		result.setPreferredSize(new Dimension(35, 20));
		return result;
	}

	public JTable getTextTable() {
		if (null == textTable) {
			textTable = new JTable(new DefaultTableModel(new Object[][] {}, new Object[] { "" }) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			TableColumn column = textTable.getColumnModel().getColumn(0);
			// column.setMaxWidth(0);
			// column.setMinWidth(0);
			// column.setPreferredWidth(0);
			// column.setWidth(0);
			column.setCellRenderer(new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					return super.getTableCellRendererComponent(table, ((Text) value).getName(), isSelected, hasFocus,
							row, column);
				}
			});
			textTable.setFocusable(false);
			textTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() && textTable.getSelectedRow() >= 0)
						((Text) textTable.getValueAt(textTable.getSelectedRow(), 0)).requestFocus();
				}
			});
			textTable.addMouseListener(new MouseAdapter() {
				private JPopupMenu menu;

				private JPopupMenu getMune() {
					if (null == menu) {
						menu = new JPopupMenu();
						menu.add(createItem("删除"));
					}
					return menu;
				}

				private JMenuItem createItem(String text) {
					JMenuItem result = new JMenuItem(text);
					result.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int row = textTable.getSelectedRow();
							printPanel.remove((Text) textTable.getValueAt(row, 0));
							printPanel.repaint();
							((DefaultTableModel) getTextTable().getModel()).removeRow(row);
						}
					});
					return result;
				}

				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e))
						getMune().show(textTable, e.getX(), e.getY());// 弹出右键菜单
				}
			});
		}
		return textTable;
	}

	public JPanel getPrintPanel() {
		if (null == printPanel) {
			printPanel = new JPanel(null);
			printPanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1)
						createText("内容", e.getX(), e.getY(), 30, 60);
				}
			});
			printPanel.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					wTextField.setText(String.valueOf(printPanel.getWidth()));
					hTextField.setText(String.valueOf(printPanel.getHeight()));
				}
			});
		}
		return printPanel;
	}

	private JButton createViewButton() {
		JButton result = new JButton("预览");
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PreviewDialog(InvitationPriterMain.this).setVisible(true);
			}
		});
		return result;
	}

	public Double toDouble(String d) {
		return Double.valueOf(d);
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		final double x = toDouble(xTextField.getText());
		final double y = toDouble(yTextField.getText());
		final double w = toDouble(wTextField.getText());
		final double h = toDouble(hTextField.getText());
		Graphics2D g2d = (Graphics2D) graphics;
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(x, y);// 设置并应用平移变换,注意这里平移可以理解为是屏幕坐标系统的平移：平移以后,屏幕坐标的原点位于(150,150)
		g2d.transform(transform);
		g2d.rotate(Double.valueOf(dTextField.getText()) * Math.PI / 180, w / 2, h / 2);// 旋转文本
		printPanel.printComponents(graphics);
		return 0 == pageIndex ? Printable.PAGE_EXISTS : Printable.NO_SUCH_PAGE;
	}

	private void initJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> property = mapper.readValue(new File(jsonFileName), Map.class);
		List<Map<String, Object>> textList = (List<Map<String, Object>>) property.get("texts");
		for (Map<String, Object> map : textList) {
			Text text = createText((String) map.get("contain"), new BigDecimal(map.get("x").toString()).intValue(),
					new BigDecimal(map.get("y").toString()).intValue(),
					new BigDecimal(map.get("w").toString()).intValue(),
					new BigDecimal(map.get("h").toString()).intValue());
			int stlye = (Boolean) map.get("bold") ? Font.BOLD : Font.PLAIN;
			if ((Boolean) map.get("italic"))
				stlye = stlye | Font.ITALIC;
			text.setFont(TTF.getFont(stlye, new BigDecimal(map.get("size").toString()).floatValue()));
		}
		xTextField.setText(String.valueOf(property.get("x")));
		yTextField.setText(String.valueOf(property.get("y")));
		wTextField.setText(String.valueOf(property.get("w")));
		hTextField.setText(String.valueOf(property.get("h")));
		dTextField.setText(String.valueOf(property.get("d")));
		setSize(new BigDecimal(property.get("pw").toString()).intValue(),
				new BigDecimal(property.get("ph").toString()).intValue());
	}

	private void saveJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> property = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> textList = new ArrayList<Map<String, Object>>();
		for (Component comp : printPanel.getComponents()) {
			if (comp instanceof Text) {
				Text textComp = (Text) comp;
				Map<String, Object> textMap = new LinkedHashMap<String, Object>();
				textMap.put("contain", textComp.getName());
				textMap.put("x", textComp.getBounds().getX());
				textMap.put("y", textComp.getBounds().getY());
				textMap.put("w", textComp.getBounds().getWidth());
				textMap.put("h", textComp.getBounds().getHeight());
				textMap.put("size", textComp.getFont().getSize());
				textMap.put("bold", textComp.getFont().isBold());
				textMap.put("italic", textComp.getFont().isItalic());
				textList.add(textMap);
			}
		}
		property.put("texts", textList);
		property.put("x", toDouble(xTextField.getText()));
		property.put("y", toDouble(yTextField.getText()));
		property.put("w", toDouble(wTextField.getText()));
		property.put("h", toDouble(hTextField.getText()));
		property.put("d", toDouble(dTextField.getText()));
		property.put("pw", getWidth());
		property.put("ph", getHeight());
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonFileName), property);
	}

	private JButton getPrintButton() {
		if (null == printButton) {
			printButton = new JButton("打印");
			printButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						saveJson();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					Book book = new Book();// 通俗理解就是书、文档
					PageFormat pf = new PageFormat();
					pf.setOrientation(PageFormat.PORTRAIT);// 设置成竖打
					final double x = toDouble(xTextField.getText());
					final double y = toDouble(yTextField.getText());
					final double w = toDouble(wTextField.getText());
					final double h = toDouble(hTextField.getText());
					final Paper p = new Paper();// 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
					// p.setSize(590, 840);// 纸张大小
					// System.out.println(getPrintPanel().getSize());
					p.setImageableArea(0, 0, w + x, h + y);// A4(595X842)设置打印区域，其实0，0应该是72，72，因为A4纸的默认X,Y边距是72
					pf.setPaper(p);
					// 把 PageFormat 和 Printable 添加到书中，组成一个页面
					book.append(new Printable() {
						@Override
						public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
								throws PrinterException {
							return InvitationPriterMain.this.print(graphics, pageFormat, pageIndex);
						}
					}, pf);
					PrinterJob job = PrinterJob.getPrinterJob();// 获取打印服务对象
					job.setPageable(book);// 设置打印类
					try {
						boolean a = job.printDialog();// 可以用printDialog显示打印对话框，在用户确认后打印；也可以直接打印
						if (a) {
							job.print();
						} else {
							job.cancel();
						}
					} catch (PrinterException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return printButton;
	}

	private void loadData() throws Exception {
		File file = new File(dataFileName);
		// 创建一个工作簿
		Workbook workbook = Workbook.getWorkbook(file);
		// 获得所有工作表
		Sheet[] sheets = workbook.getSheets();
		if (sheets.length < 1) {
			throw new Exception("无效数据");
		}
		Sheet sheet = sheets[0];
		int rows = sheet.getRows();// 获得行数
		int cols = sheet.getColumns();// 获得列数
		// 读取数据
		DefaultTableModel model = (DefaultTableModel) loopTable.getModel();
		model.setRowCount(0);
		model.setColumnCount(0);
		for (int col = 0; col < cols; col++)
			model.addColumn(sheet.getCell(col, 0).getContents().trim());
		for (int row = 1; row < rows; row++) {
			Object[] rowdata = new Object[cols];
			for (int col = 0; col < cols; col++)
				rowdata[col] = sheet.getCell(col, row).getContents().trim();
			model.addRow(rowdata);
		}
		workbook.close();
	}

	private Text createText(String text, int x, int y, int w, int h) {
		final Text textArea = new Text() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void refreshStatus(Text comp) {
				propertyPanel.setSelectTextComp(comp);
			}

			@Override
			public void setName(String name) {
				if (!name.equals(getName()))
					textTable.repaint();
				super.setName(name);
			}
		};
		try {
			textArea.setFont(TTF.getFont(Font.PLAIN, 30));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// textArea.setEditable(false);
		textArea.requestFocus();
		textArea.setName(text);
		textArea.setBounds(x, y, w, h);
		printPanel.add(textArea);
		((DefaultTableModel) getTextTable().getModel()).addRow(new Object[] { textArea });
		// repaint();
		return textArea;
	}
}
