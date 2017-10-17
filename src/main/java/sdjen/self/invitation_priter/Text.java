package sdjen.self.invitation_priter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

public abstract class Text extends JTextArea {
	private static final long serialVersionUID = 1L;

	public Text() {
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				refreshStatus(Text.this);
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}

			@Override
			public void focusLost(FocusEvent e) {
				setBorder(BorderFactory.createEmptyBorder());
			}
		});
		setLineWrap(true);
		setOpaque(false);
		MouseAdapter adapter = new MouseAdapter() {
			// 这两组x和y为鼠标点下时在屏幕的位置和拖动时所在的位置
			int newX, newY, oldX, oldY;
			// 这两个坐标为组件当前的坐标
			int startX, startY;

			@Override
			public void mousePressed(MouseEvent e) {
				// 此为得到事件源组件
				Component cp = (Component) e.getSource();
				// 当鼠标点下的时候记录组件当前的坐标与鼠标当前在屏幕的位置
				startX = cp.getX();
				startY = cp.getY();
				oldX = e.getXOnScreen();
				oldY = e.getYOnScreen();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				java.awt.Point p = getLocation();
				if (getCursor().getType() == Cursor.W_RESIZE_CURSOR) {
					Point point = getLocation();
					p.x = e.getPoint().x + point.x - 0;
					p.y = point.y;
					setLocation(p);
					int w = getSize().width - e.getPoint().x;
					int h = getSize().height;
					setSize(w, h);
				} else if (getCursor().getType() == Cursor.E_RESIZE_CURSOR) {
					int w = e.getPoint().x;
					int h = getSize().height;
					setSize(w, h);
					setLocation(p);
				} else if (getCursor().getType() == Cursor.N_RESIZE_CURSOR) {
					int h = e.getPoint().y;
					int w = getSize().width;
					setSize(w, h);
					setLocation(p);
				} else if (getCursor().getType() == Cursor.S_RESIZE_CURSOR) {
					Point point = getLocation();
					p.y = e.getPoint().y + point.y - 0;
					p.x = point.x;
					setLocation(p);
					int h = getSize().height - e.getPoint().y;
					int w = getSize().width;
					setSize(w, h);
				} else {
					// 拖动的时候记录新坐标
					newX = e.getXOnScreen();
					newY = e.getYOnScreen();
					// 设置bounds,将点下时记录的组件开始坐标与鼠标拖动的距离相加
					setBounds(startX + (newX - oldX), startY + (newY - oldY), getWidth(), getHeight());
				}
				refreshStatus(Text.this);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (getSize().width - 8 <= e.getX()) {
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));// 右
				} else if (8 >= e.getX()) {
					setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));// 左
				} else if (getSize().height - 8 <= e.getY()) {
					setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));// 左
				} else if (8 >= e.getY()) {
					setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));// 左
				} else {
					setCursor(Cursor.getDefaultCursor());
				}
			};
		};
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	protected abstract void refreshStatus(Text comp);
}
