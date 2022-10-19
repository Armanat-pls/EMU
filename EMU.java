import java.util.BitSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javafx.scene.layout.Border;
public class EMU extends secondary{  

    static class UI extends JFrame {
        //кнопки 
        private JButton button_set_CANT = new JButton("Установить счётчик");
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
            
            container.add(label_CANT_out);
            container.add(textBox_CANT);
            container.add(textBox_CURcell);
            
            label_ALU_out.setBounds(10,60, 365, 55);
            textBox_RO.setBounds(10,100, 60, 25);
            textBox_ALU.setBounds(80,100, 300, 25);
            
            container.add(label_ALU_out);
            container.add(textBox_RO);
            container.add(textBox_ALU);
            
            textBox_CANT.setEditable(false);
            textBox_CURcell.setEditable(false);
            textBox_RO.setEditable(false);
            textBox_ALU.setEditable(false);
            textBox_RO.setText("[RO]");
            
            label_RAM_list.setBounds(10,120, 365, 55);
            list_RAM_tmp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list_RAM_tmp.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e){
                    //Object element = list_RAM_tmp.getSelectedValue();
                    //label_RAM_list.setText(element.toString());
                    label_RAM_list.setText("" + list_RAM_tmp.getSelectedIndex());
                    RAM_choser.setValue(list_RAM_tmp.getSelectedIndex());
                }
            });
            list_RAM_final.setBounds(10,160, 370, 430);
            container.add(label_RAM_list);
            container.add(list_RAM_final);






            label_RAM_chooser.setBounds(420, 15, 90, 30);
            RAM_choser.setBounds(420, 40, 75, 30);
            container.add(label_RAM_chooser);
            container.add(RAM_choser);








            refreshUI();     

            /* 
    
            ButtonGroup group = new ButtonGroup();
            group.add(radio1);
            group.add(radio2);

            container.add(radio1);
            radio1.setSelected(true);

            container.add(radio2);
            container.add(check);
            button.addActionListener(new ButtonEventListener());
            container.add(button);
            */
            
        }

        private void refreshUI(){
            textBox_CANT.setText("[" + UU.CANT + "]");
            textBox_CURcell.setText(RAM.show_cell(UU.CANT));
            textBox_ALU.setText(ALU.showRO());
            listRAM.removeAllElements();
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
                listRAM.addElement(S);
            }
        }

        private void refresh_RAM_out(){
            BitSet[] coms = cut_com(RAM.get_cell(0));  //вызов деления
            int C = (int)coms[0].toLongArray()[0];
            int A = (int)coms[1].toLongArray()[0];
        }

        /*
        class ButtonEventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                String message = "";
                message += "Button was pressed\n";
                message += "Text is " + input.getText() + "\n";
                message += (radio1.isSelected()?"Radio #1":"Radio #2") 
                                    + " is selected\n"; 
                message += "CheckBox is " + ((check.isSelected())
                                    ?"checked":"unchecked"); 
                JOptionPane.showMessageDialog(null,
                        message,
                        "Output",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }*/
    }
    public static void main(String[] args) {
		UI app = new UI();
		app.setVisible(true);
	}

}

