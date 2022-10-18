import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
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
        private JList list_RAM = new JList<String>();

        //Выборщик ячейки
        private JLabel label_RAM_chooser = new JLabel("Выбор ячейки");
        private JSpinner RAM_choser = new JSpinner();

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
            textBox_CANT.setEditable(false);
            textBox_CURcell.setBounds(80,40, 300, 25);
            textBox_CURcell.setEditable(false);
            container.add(label_CANT_out);
            container.add(textBox_CANT);
            container.add(textBox_CURcell);
            textBox_CANT.setText("[" + UU.CANT + "]");
            textBox_CURcell.setText(RAM.show_cell(UU.CANT));


            label_ALU_out.setBounds(10,60, 365, 55);
            textBox_RO.setBounds(10,100, 60, 25);
            textBox_RO.setEditable(false);
            textBox_ALU.setBounds(80,100, 300, 25);
            textBox_ALU.setEditable(false);
            container.add(label_ALU_out);
            container.add(textBox_RO);
            container.add(textBox_ALU);
            textBox_RO.setText("[RO]");
            textBox_ALU.setText(ALU.showRO());



                    //поле вывода АЛУ
            
            //this.add("label_CANT_OUT", label);


            /* 
            Container container = this.getContentPane();
            container.setLayout(new GridLayout(6,2,2,2));
            container.add(label);
            container.add(input);
    
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

