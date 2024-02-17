package ru.vsu.cs.course1.game;

import java.util.Random;

/**
 * Класс, реализующий логику игры
 */
public class Game {

    /**
     * объект Random для генерации случайных чисел
     * (можно было бы объявить как static)
     */
    private final Random rnd = new Random();

    /**
     * двумерный массив для хранения игрового поля
     * (в данном случае цветов, 0 - пусто; создается / пересоздается при старте игры)
     */
    private int[][] field = null;
    /**
     * Максимальное кол-во цветов
     */
    private int colorCount = 0;

    private int flag = 1;
    private int flagCell = 1;

    public Game() {
    }

    public void newGame(int rowCount, int colCount, int colorCount) {
        // создаем поле
        field = new int[rowCount][colCount];
        this.colorCount = colorCount;
    }

    public void leftMouseClick(int row, int col) {
        int rowCount = getRowCount(), colCount = getColCount();
        if (row < 0 || row >= rowCount || col < 0 || col >= colCount) {
            return;
        }

        addNumber(rowCount,colCount);
    }

    public void rightMouseClick(int row, int col) {
        int rowCount = getRowCount(), colCount = getColCount();
        if (row < 0 || row >= rowCount || col < 0 || col >= colCount) {
            return;
        }
        addNumber(rowCount, colCount);
    }

    public void addNumber(int row, int col){ // добавить цифру
        flagCell = 0;
        int pos = rnd.nextInt(row * col);
        int num = 2;
        if(pos > 10){
            num = 4;
        }
        for(int i = 0; i < 16; i++){
            for (int x = 0; x < row; x++) {
                for (int y = 0; y < col; y++) {
                    if (field[x][y] == 0 && pos <= 0) {
                        field[x][y] = num;
                        flagCell = 1;
                        return;
                    }
                    pos--;
                }
            }
        }

    }

    public void upButton(){
        flag = 0;
        for (int i = 0; i < 4; i++) {
            for (int row = 1; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    if(field[row][col] != 0 && field[row-1][col] == 0){
                        field[row-1][col] = field[row][col];
                        field[row][col] = 0;
                        flag = 1;
                    } else if (field[row][col] != 0 && field[row - 1][col] == field[row][col]) {
                        field[row-1][col] = field[row][col] * 2;
                        field[row][col] = 0;
                        flag = 1;
                    }
                }
            }
        }
    }
    public void downButton(){
        flag = 0;
        for (int i = 0; i < 4; i++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 4; col++) {
                    if(field[row][col] != 0 && field[row+1][col] == 0){
                        field[row+1][col] = field[row][col];
                        field[row][col] = 0;
                        flag = 1;
                    } else if (field[row][col] != 0 && field[row + 1][col] == field[row][col]) {
                        field[row+1][col] = field[row][col] * 2;
                        field[row][col] = 0;
                        flag = 1;
                    }
                }
            }
        }
    }
    public void rightButton(){
        flag = 0;
        for (int i = 0; i < 4; i++) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 3; col++) {
                    if(field[row][col] != 0 && field[row][col+1] == 0){
                        field[row][col+1] = field[row][col];
                        field[row][col] = 0;
                        flag = 1;
                    } else if (field[row][col] != 0 && field[row][col+1] == field[row][col]) {
                        field[row][col+1] = field[row][col] * 2;
                        field[row][col] = 0;
                        flag = 1;
                    }
                }
            }
        }
    }
    public void leftButton(){
        flag = 0;
        for (int i = 0; i < 4; i++) {
            for (int row = 0; row < 4; row++) {
                for (int col = 1; col < 4; col++) {
                    if(field[row][col] != 0 && field[row][col-1] == 0){
                        field[row][col-1] = field[row][col];
                        field[row][col] = 0;
                        flag = 1;
                    } else if (field[row][col] != 0 && field[row][col-1] == field[row][col]) {
                        field[row][col-1] = field[row][col] * 2;
                        field[row][col] = 0;
                        flag = 1;
                    }
                }
            }
        }
    }

    public boolean IsFinish(){
        return flagCell + flag == 0;
    }
    public int getRowCount() {
        return field == null ? 0 : field.length;
    }

    public int getColCount() {
        return field == null ? 0 : field[0].length;
    }


    public int getCell(int row, int col) {
        return (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) ? 0 : field[row][col];
    }
}
