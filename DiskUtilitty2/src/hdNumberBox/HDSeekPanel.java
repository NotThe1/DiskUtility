package hdNumberBox;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class HDSeekPanel extends HDNumberBox {

	private static final long serialVersionUID = 1L;

	private int stepValue;
	private AdapterForPanel adapterForPanel = new AdapterForPanel();

	private void stepValue(int direction) {
		int changeAmount = stepValue * direction;

		long newValue = currentValue;
		newValue += changeAmount;

		if (newValue > rangeModel.getMaximum()) {
			setNewValue(rangeModel.getMaximum());
		} else if (newValue < rangeModel.getMinimum()) {
			setNewValue(rangeModel.getMinimum());
		} else {
			setNewValue((int) newValue);
		} // if
	}//

	// -------------------------------------------------------

	public HDSeekPanel() {
		this(true);
	}// Constructor

	public HDSeekPanel(boolean decimalDisplay) {
		super(decimalDisplay);
		initialize();
		appInit();
	}// Constructor

	///////////////////////////////////////////////////////////////////////////
	private void setStepValue(int step) {
		this.stepValue = Math.max(1, step); // must be 1 or greater
	}//setStepValue
	
	private void doFirst() {
		setNewValue( rangeModel.getMinimum());
	}// doFirst

	private void doLast() {
		setNewValue( rangeModel.getMaximum());
	}// doLast

	private void doNext() {
		stepValue(UP);
	}// doNext

	private void doPrevious() {
		stepValue(DOWN);
	}// doPrevious

	///////////////////////////////////////////////////////////////////////////
	
	
	private void appInit() {
		this.addMouseListener(adapterForPanel);
		this.setStepValue(1);
	}//appInit

	public void initialize() {

		setPreferredSize(new Dimension(385, 30));

		JButton btnFirst = new JButton("<<");
		btnFirst.setMaximumSize(new Dimension(0, 0));
		btnFirst.setName(FIRST);
		btnFirst.addActionListener(adapterForPanel);
		setLayout(new GridLayout(0, 5, 0, 0));
		add(btnFirst);

		JButton btnPrior = new JButton("<");
		btnPrior.setName(PREVIOUS);
		btnPrior.addActionListener(adapterForPanel);
		add(btnPrior);


		add(txtValueDisplay);
		
		JButton btnNext = new JButton(">");
		btnNext.setName(NEXT);
		btnNext.addActionListener(adapterForPanel);
		
		add(btnNext);

		JButton btnLast = new JButton(">>");
		btnLast.setName(LAST);
		btnLast.addActionListener(adapterForPanel);
		add(btnLast);

		setBorder(UIManager.getBorder("Spinner.border"));
	}// Constructor

	// ---------------------------

	class AdapterForPanel implements ActionListener, MouseListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((JComponent) actionEvent.getSource()).getName();
			switch (name) {
			case FIRST:
				doFirst();
				break;
			case LAST:
				doLast();
				break;
			case NEXT:
				doNext();
				break;
			case PREVIOUS:
				doPrevious();
				break;
			}// switch

		}// actionPerformed

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() > 1) {
				if (showDecimal) {
					setHexDisplay();
				} else {
					setDecimalDisplay();
				} // if inner
			} // if click count
		}// mouseClicked

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {
			/* Not Used */
		}// mouseEntered

		@Override
		public void mouseExited(MouseEvent mouseEvent) {
			/* Not Used */
		}// mouseExited

		@Override
		public void mousePressed(MouseEvent mouseEvent) {
			/* Not Used */
		}// mousePressed

		@Override
		public void mouseReleased(MouseEvent arg0) {
			/* Not Used */
		}// mouseReleased

	}// class AdapterForPanel

	private static final int UP = 1;
	private static final int DOWN = -1;

	private static final String FIRST = "First";
	private static final String LAST = "Last";
	private static final String NEXT = "Next";
	private static final String PREVIOUS = "Previous";
}// class HDSeekPanel
