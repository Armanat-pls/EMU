import java.util.BitSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.event.ActionEvent.*;

public class EMU extends secondary{  

    static class UI extends JFrame {
        //кнопки 
        private JButton button_set_CANT = new JButton("<html>Установить<p>Счётчик</html>"); 
        private JButton button_1cell = new JButton("Выполнить текущую ячейку");
        private JButton button_runALL = new JButton("Выполнить программу");

        //поле вывода СЧАК
        private JLabel label_CANT_out = new JLabel("Счётчик команд и активная ячейка");
        private JTextField textBox_CANT = new JTextField();
        private JTextField textBox_CURcell = new JTextField();

        //поле вывода АЛУ
        private JLabel label_ALU_out = new JLabel("Регистр АЛУ");
        private JTextField textBox_RO = new JTextField();
        private JTextField textBox_ALU = new JTextField();

        //Список RAM
        private JLabel label_RAM_list = new JLabel("RAM");
        private DefaultListModel<String> listRAM = new DefaultListModel<String>();
        private JList<String> list_RAM_tmp = new JList<String>(listRAM);
        private JScrollPane list_RAM_final = new JScrollPane(list_RAM_tmp);

        //поля вывода значений ячейки
        private JLabel label_RAM_out = new JLabel("Содержимое ячейки");
        private JTextField textBox_RAM_out_com_1 = new JTextField();
        private JTextField textBox_RAM_out_com_2 = new JTextField();
        private JTextField textBox_RAM_out_int = new JTextField();
        private JTextField textBox_RAM_out_float = new JTextField();
        private JLabel label_RAM_out_com = new JLabel("Команда");
        private JLabel label_RAM_out_int = new JLabel("Целое");
        private JLabel label_RAM_out_float = new JLabel("Дробное");

        //Выборщик ячейки
        private JLabel label_RAM_chooser = new JLabel("Выбор ячейки");
        private JSpinner RAM_choser = new JSpinner(new SpinnerNumberModel(0, 0, MEM - 1, 1));


/* 
        private JTextField input = new JTextField("", 5);
        
        private JRadioButton radio1 = new JRadioButton("Select this");
        private JRadioButton radio2 = new JRadioButton("Select that");
        private JCheckBox check = new JCheckBox("Check", false);
*/   
        public UI() {
            super("Учебный эмулятор ЭВМ " + VER);
            this.setBounds(100,100,900,675);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Container container = this.getContentPane();
            container.setLayout(null);

            label_CANT_out.setBounds(10,0, 365, 55);
            textBox_CANT.setBounds(10,40, 60, 25);
            textBox_CURcell.setBounds(80,40, 300, 25);
        
            label_ALU_out.setBounds(10,60, 365, 55);
            textBox_RO.setBounds(10,100, 60, 25);
            textBox_ALU.setBounds(80,100, 300, 25);
            
            textBox_CANT.setEditable(false);
            textBox_CURcell.setEditable(false);
            textBox_RO.setEditable(false);
            textBox_ALU.setEditable(false);
            textBox_RO.setText("[RO]");
            
            label_RAM_list.setBounds(10,120, 365, 55);
            list_RAM_tmp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list_RAM_tmp.addListSelectionListener(new ListSelectionListener() { //ИЗМЕНЕНИЕ ВЫБРАННОЙ ЯЧЕЙКИ В СПИСКЕ
                public void valueChanged(ListSelectionEvent e){
                    RAM_choser.setValue(list_RAM_tmp.getSelectedIndex());
                }
            });
            list_RAM_final.setBounds(10,160, 370, 430);


            label_RAM_out.setBounds(400,300, 480, 25);

            label_RAM_out_com.setBounds(430,330, 80, 25);
            textBox_RAM_out_com_1.setBounds(500,330, 200, 25);
            textBox_RAM_out_com_2.setBounds(710,330, 80, 25);

            label_RAM_out_int.setBounds(430,360, 365, 25);
            textBox_RAM_out_int.setBounds(500,360, 200, 25);

            label_RAM_out_float.setBounds(430,390, 365, 25);
            textBox_RAM_out_float.setBounds(500,390, 200, 25);
            
            textBox_RAM_out_com_1.setEditable(false);
            textBox_RAM_out_com_2.setEditable(false);
            textBox_RAM_out_int.setEditable(false);
            textBox_RAM_out_float.setEditable(false);

            label_RAM_chooser.setBounds(420, 15, 90, 30);
            RAM_choser.setBounds(420, 40, 75, 30);
            RAM_choser.addChangeListener(new RAM_chooser_Listener());


            button_set_CANT.setBounds(545, 15, 100, 65);
            button_set_CANT.addActionListener(new Button_SETCANT_EventListener());
            





            container.add(label_ALU_out);
            container.add(textBox_RO);
            container.add(textBox_ALU);
            container.add(label_CANT_out);
            container.add(textBox_CANT);
            container.add(textBox_CURcell);
            container.add(label_RAM_list);
            container.add(list_RAM_final);
            container.add(label_RAM_out);
            container.add(label_RAM_out_com);
            container.add(label_RAM_out_int); 
            container.add(label_RAM_out_float);
            container.add(textBox_RAM_out_com_1);
            container.add(textBox_RAM_out_com_2);
            container.add(textBox_RAM_out_int);
            container.add(textBox_RAM_out_float);
            container.add(label_RAM_chooser);
            container.add(RAM_choser);
            container.add(button_set_CANT);
            refreshUI();     

            /* 
    
            ButtonGroup group = new ButtonGroup();
            group.add(radio1);
            group.add(radio2);

            container.add(radio1);
            radio1.setSelected(true);

            container.add(radio2);
            container.add(check);
            
            container.add(button);
            */
            
        }

        //КНОПКА ВЫСТАВЛЕНИЯ СЧАК
        class Button_SETCANT_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                try{
                    UU.CANT = (int)RAM_choser.getValue();
                }
                catch (Throwable t)
                {
                    JOptionPane.showMessageDialog(null, "Ошибка ввода счётчика", "Eroor", JOptionPane.PLAIN_MESSAGE);
                }
                refreshUI();
            }
        }

        //СОБЫТИЕ ВЫБОРА ЯЧЕЙКИ СПИНЕРОМ (выделеняет ячейку в списке)
        class RAM_chooser_Listener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
               list_RAM_tmp.setSelectedIndex((int)RAM_choser.getValue());
               refresh_RAM_out();
            }
         }

        //ПЕРЕЗАГРУЗКА UI
        private void refreshUI(){
            textBox_CANT.setText("[" + UU.CANT + "]");
            textBox_CURcell.setText(RAM.show_cell(UU.CANT));
            textBox_ALU.setText(ALU.showRO());
            boolean first = false;
            if (listRAM.isEmpty())
                first = true;
            String S = "";
            for (int i = 0; i < MEM; i++)
            {
                if (i < 10)
                    S = "[00" + i + "]: ";
                else if (i < 100)
                    S = "[0" + i + "]: ";
                else if (i < 1000)
                    S = "[" + i + "]: ";
                S += RAM.show_cell(i);
                if (first)
                    listRAM.addElement(S);
                else
                    listRAM.set(i,S);
            }
            list_RAM_tmp.setSelectedIndex(UU.CANT);
        }
        private void refresh_RAM_out(){
            textBox_RAM_out_int.setText("" + bit_to_int(RAM.get_cell((int)RAM_choser.getValue())));
            textBox_RAM_out_float.setText("" + bit_to_float(RAM.get_cell((int)RAM_choser.getValue())));

            BitSet[] coms = new BitSet[2];
            int C;
            int A;
            coms = cut_com(RAM.get_cell((int)RAM_choser.getValue()));  //вызов деления
            if (coms[0].isEmpty())
                C = 0;
            else
                C = (int)coms[0].toLongArray()[0];
            if (coms[1].isEmpty())
                A = 0;
            else
                A = (int)coms[1].toLongArray()[0];

            textBox_RAM_out_com_1.setText("" + C);
            textBox_RAM_out_com_2.setText("" + A);
        }
        

    }
    public static void main(String[] args) {
		UI app = new UI();
		app.setVisible(true);
	}

}

