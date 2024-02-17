package ru.vsu.cs.course1.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JTable tableGameField;
    private JLabel labelStatus;
    private JLabel gameStatus;

    private static final int DEFAULT_COL_COUNT = 4;
    private static final int DEFAULT_ROW_COUNT = 4;
    private static final int DEFAULT_COLOR_COUNT = 7;

    private static final int DEFAULT_GAP = 8;
    private static final int DEFAULT_CELL_SIZE = 150;//размер ячейки по дефолту

    private static final Color[] COLORS = {
            Color.BLUE,
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.MAGENTA,
            Color.CYAN,
            Color.ORANGE,
            Color.PINK,
            Color.WHITE,
            Color.GRAY
    };
    private boolean winGame = false;
    private GameParams params = new GameParams(DEFAULT_ROW_COUNT, DEFAULT_COL_COUNT, DEFAULT_COLOR_COUNT);
    private Game game = new Game();

    /* Демонстрация работы с таймером (удалить, если не нужно в вашей игре) */
    private int time = 0;
    private Timer timer = new Timer(1000, e -> {
        time++;
        this.labelStatus.setText("Прошло времени (секунд): " + time);
    });


    private ParamsDialog dialogParams;


    public MainForm() {
        this.setTitle("2048");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        setJMenuBar(createMenuBar());
        this.pack();

        SwingUtils.setShowMessageDefaultErrorHandler();

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false, false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);

        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { // рендер для отображения содержимого ячейки
            // механизм нужен для отрисовки каждой отделььной ячейки, реализован для того, чтобы вызвать метод paintCellWin
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 2;
                    int height = getHeight() - 2;
                    paintCell(row, column, g2d, width, height);
                    if (winGame) {
                        paintCellWin(row, column, g2d, width, height);
                    }
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        dialogParams = new ParamsDialog(params, tableGameField, e -> newGame());

        tableGameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tableGameField.rowAtPoint(e.getPoint());
                int col = tableGameField.columnAtPoint(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    game.leftMouseClick(row, col);
                    updateView();
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    game.rightMouseClick(row, col);
                    updateView();
                }
            }
        });


        /*
            обработка событий нажатия клавиш (если в вашей программе не нужно, удалить код ниже)
            сделано так, а не через addKeyListener, так в последнем случае события будет получать компонент с фокусом,
            т.е. если на форме есть, например, кнопка или поле ввода, то все события уйдут этому компоненту
         */
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) { // обработчик нажатий
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == 38 || e.getKeyCode() == 87) {
                        game.upButton();
                        game.addNumber(game.getRowCount(), game.getColCount());
                        if (game.IsFinish()) {
                            restart();
                        }
                        updateView();


                    } else if (e.getKeyCode() == 40 || e.getKeyCode() == 83) {
                        game.downButton();
                        game.addNumber(game.getRowCount(), game.getColCount());
                        if (game.IsFinish()) {
                            restart();
                        }
                        updateView();


                    } else if (e.getKeyCode() == 39 || e.getKeyCode() == 68) {
                        game.rightButton();
                        game.addNumber(game.getRowCount(), game.getColCount());
                        if (game.IsFinish()) {
                            restart();
                        }
                        updateView();

                    } else if (e.getKeyCode() == 37 || e.getKeyCode() == 65) {
                        game.leftButton();
                        game.addNumber(game.getRowCount(), game.getColCount());
                        if (game.IsFinish()) {
                            restart();
                        }
                        updateView();
                    } else if (e.getKeyCode() == 32) {
                        winGame = false;
                        restart();
                    }
                }

                return false;
            }
        });
    }

    private JMenuItem createMenuItem(String text, String shortcut, Character mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    private void restart() {
        dialogParams.restart();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();

        JMenu menuGame = new JMenu("Игра");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("Новая", "ctrl+N", null, e -> {
            newGame();
            winGame = false;
        }));
        menuGame.add(createMenuItem("Параметры", "ctrl+P", null, e -> {
            dialogParams.updateView();
            dialogParams.setVisible(true);
        }));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Выход", "ctrl+X", null, e -> {
            System.exit(0);
        }));

        JMenu menuView = new JMenu("Вид");
        menuBarMain.add(menuView);
        menuView.add(createMenuItem("Подогнать размер окна", null, null, e -> {
            updateWindowSize();
        }));
        menuView.addSeparator();
        SwingUtils.initLookAndFeelMenu(menuView);

        JMenu menuHelp = new JMenu("Справка");
        menuBarMain.add(menuHelp);
        menuHelp.add(createMenuItem("Правила", "ctrl+R", null, e -> {
            SwingUtils.showInfoMessageBox("Здесь должно быть краткое описание правил ...", "Правила");
        }));
        menuHelp.add(createMenuItem("О программе", "ctrl+A", null, e -> {
            SwingUtils.showInfoMessageBox(
                    "Игра 2048" +
                            "\n\nАвтор: Исаев Н.С.",
                    "О программе"
            );
        }));

        return menuBarMain;
    }

    private void updateWindowSize() { //обновление размеров окна
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP + 60,
                tableGameField.getHeight() + panelMain.getY() + labelStatus.getHeight() +
                        menuSize + DEFAULT_GAP + 2 * DEFAULT_GAP + 60
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() { //отвечает за обновление состояния поля
        tableGameField.repaint();
    }


    private Font font = null;

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Comic Sans MS", Font.BOLD, size);
        }
        return font;
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        int cellValue = game.getCell(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cellValue <= 0) {
            return;
        }
        int numberCellValue = (int) (Math.log(cellValue) / Math.log(2));
        int buffer = 1;
        if (numberCellValue == 11) {
            winGame = true;
            buffer = 2;
        }
        Color color = COLORS[numberCellValue - buffer];

        int size = Math.min(cellWidth, cellHeight);
        int bound = (int) Math.round(size * 0.1);

        g2d.setColor(color);
        g2d.fillRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);

        if (numberCellValue >= 10) {
            g2d.setFont(getFont(size - 7 * bound));
        } else if (numberCellValue > 6) {
            g2d.setFont(getFont(size - 6 * bound));
        } else {
            g2d.setFont(getFont(size - 4 * bound));
        }
        g2d.setColor(DrawUtils.getContrastColor(color));
        DrawUtils.drawStringInCenter(g2d, font, "" + cellValue, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));
    }

    private void paintCellWin(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = COLORS[4];
        int size = Math.min(cellWidth, cellHeight);
        int bound = (int) Math.round(size * 0.1);
        g2d.setColor(color);
        g2d.fillRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);

        g2d.setFont(getFont(size - 4 * bound));
        g2d.setColor(DrawUtils.getContrastColor(color));
        String buffer = " ";
        if (row == 1 && column == 0) {
            buffer = "Y";
        } else if (row == 1 && column == 1) {
            buffer = "O";
        } else if (row == 1 && column == 2) {
            buffer = "U";
        } else if (row == 2 && column == 1) {
            buffer = "W";
        } else if (row == 2 && column == 2) {
            buffer = "I";
        } else if (row == 2 && column == 3) {
            buffer = "N";
        } else return;
        DrawUtils.drawStringInCenter(g2d, font, buffer, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));
    }

    private void newGame() {
        game.newGame(params.getRowCount(), params.getColCount(), params.getColorCount());
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        time = 0;
        timer.start();
        updateView();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, 10));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableGameField = new JTable();
        scrollPane1.setViewportView(tableGameField);
        labelStatus = new JLabel();
        labelStatus.setText("Label");
        panelMain.add(labelStatus, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gameStatus = new JLabel();
        Font gameStatusFont = this.$$$getFont$$$("Comic Sans MS", Font.BOLD, 36, gameStatus.getFont());
        if (gameStatusFont != null) gameStatus.setFont(gameStatusFont);
        gameStatus.setText("GAME 2048");
        panelMain.add(gameStatus, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
