import java.util.BitSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EMU extends secondary{  

    static class UI extends JFrame {
        //кнопки 
        private JButton button_set_CANT = new JButton("<html>Установить<p>Счётчик</html>"); 
        private JButton button_1cell = new JButton("<html>Выполнить<p>текущую<p>ячейку</html>"); 
        private JButton button_runALL = new JButton("<html>Выполнить<p>программу</html>");

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

        //Запись памяти
        private JLabel label_RAM_in = new JLabel("Запись ячейки");
        private JRadioButton Rbutton_RAN_Cell_cng_clean = new JRadioButton("Прямой ввод");
        private JRadioButton Rbutton_RAN_Cell_cng_comm = new JRadioButton("Команда");
        private JRadioButton Rbutton_RAN_Cell_cng_data = new JRadioButton("Данные");

        //чистая запись
        private JTextField textBox_ram_write_clean = new JTextField();
        private JLabel label_cleanwrite_msg = new JLabel("Битовый набор без пробелов (32bit)");
        private JButton btn_ramwrite_clean = new JButton("Ввод"); 

        //запись команды
        private JTextField textBox_ram_write_comm_c = new JTextField();
        private JTextField textBox_ram_write_comm_addr = new JTextField();
        private JLabel label_comwrite_msg = new JLabel("Команда; адрес операнда");
        private JButton btn_ramwrite_coms = new JButton("Ввод"); 



        private JCheckBox checkBox_writetoALU = new JCheckBox("Запись в регистр АЛУ", false);

        public UI() {
            super("Учебный эмулятор ЭВМ " + VER);
            this.setBounds(100,100,900,675);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Container container = this.getContentPane();
            container.setLayout(null);

//======================================================
//          Вывод активной ячейки и регистра АЛУ

            int baseX = 10;    //базовые координаты блока
            int baseY = 0;
            label_CANT_out.setBounds(baseX, baseY, 365, 55);
            textBox_CANT.setBounds(baseX,baseY + 40, 60, 25);
            textBox_CURcell.setBounds(baseX + 70,baseY + 40, 300, 25);
        
            baseX = 10;
            baseY = 60;
            label_ALU_out.setBounds(baseX, baseY, 365, 55);
            textBox_RO.setBounds(baseX, baseY + 40, 60, 25);
            textBox_ALU.setBounds(baseX + 70, baseY + 40, 300, 25);
            
            textBox_CANT.setEditable(false);
            textBox_CURcell.setEditable(false);
            textBox_RO.setEditable(false);
            textBox_ALU.setEditable(false);
            textBox_RO.setText("[RO]");


//======================================================
//          Вывод списка ячеек RAM

            baseX = 10;
            baseY = 120;
            label_RAM_list.setBounds(baseX, baseY, 365, 55);
            list_RAM_tmp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list_RAM_tmp.addListSelectionListener(new ListSelectionListener() { //ИЗМЕНЕНИЕ ВЫБРАННОЙ ЯЧЕЙКИ В СПИСКЕ
                public void valueChanged(ListSelectionEvent e){
                    RAM_choser.setValue(list_RAM_tmp.getSelectedIndex());
                }
            });
            list_RAM_final.setBounds(baseX, baseY + 40 , 370, 430);




//======================================================
//          Выборщик ячейки

            baseX = 420;
            baseY = 15;
            label_RAM_chooser.setBounds(baseX, baseY, 90, 25);
            RAM_choser.setBounds(baseX + 5, baseY + 25, 75, 25);
            RAM_choser.addChangeListener(new RAM_chooser_Listener());


//======================================================
//          Кнопки выставления CANT, пошаговой работы, полной работы

            baseX = 545;
            baseY = 15;
            button_set_CANT.setBounds(baseX, baseY, 100, 65);
            button_set_CANT.addActionListener(new Button_SETCANT_EventListener());

            button_1cell.setBounds(baseX + 110, baseY, 100, 65);
            button_1cell.addActionListener(new Button_1cell_EventListener());

            button_runALL.setBounds(baseX + 220, baseY, 100, 65);
            button_runALL.addActionListener(new Button_runALL_EventListener());
            

//======================================================
//          Вывод значения выбранной ячейки

            baseX = 400;
            baseY = 300;
            label_RAM_out.setBounds(baseX, baseY, 480, 25);

            label_RAM_out_com.setBounds(baseX + 30, baseY + 30, 80, 25);
            textBox_RAM_out_com_1.setBounds(baseX + 100, baseY + 30, 200, 25);
            textBox_RAM_out_com_2.setBounds(baseX + 310, baseY + 30, 80, 25);

            label_RAM_out_int.setBounds(baseX + 30, baseY + 60, 365, 25);
            textBox_RAM_out_int.setBounds(baseX + 100, baseY + 60, 200, 25);

            label_RAM_out_float.setBounds(baseX + 30, baseY + 90, 365, 25);
            textBox_RAM_out_float.setBounds(baseX + 100, baseY + 90, 200, 25);
            
            textBox_RAM_out_com_1.setEditable(false);
            textBox_RAM_out_com_2.setEditable(false);
            textBox_RAM_out_int.setEditable(false);
            textBox_RAM_out_float.setEditable(false);

            
//======================================================
//          Поля ввода ячейки 

            baseX = 400;
            baseY = 120;
            label_RAM_in.setBounds(baseX, baseY, 200, 25);
            Rbutton_RAN_Cell_cng_clean.setBounds(baseX + 20, 150, 120,25);
            Rbutton_RAN_Cell_cng_comm.setBounds(baseX + 20, 180, 120,25);
            Rbutton_RAN_Cell_cng_data.setBounds(baseX + 20, 210, 120,25);
            checkBox_writetoALU.setBounds(baseX + 20, 240, 200, 25);


            //поля чистого ввода
            textBox_ram_write_clean.setBounds(540, 150, 300, 25);
            label_cleanwrite_msg.setBounds(580, 175, 260, 25);
            btn_ramwrite_clean.setBounds(640, 200, 80, 25);

/*
            //поля ввода команды
            textBox_ram_write_comm_c
            textBox_ram_write_comm_addr
            label_comwrite_msg
            btn_ramwrite_coms
*/
            //поля ввода данных




            //радиокнопки ввода
            ButtonGroup group_Rbutton_RAM_in = new ButtonGroup();
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_clean);
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_comm);
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_data);
            Rbutton_RAN_Cell_cng_clean.setSelected(true);

            //обработчики событий радиокнопок ввода
            Rbutton_RAN_Cell_cng_clean.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {     
                    if (e.getStateChange() == 1)
                    {
                        textBox_ram_write_clean.setVisible(true);
                        label_cleanwrite_msg.setVisible(true);
                        btn_ramwrite_clean.setVisible(true);
                    }
                    else
                    {
                        textBox_ram_write_clean.setVisible(false);
                        label_cleanwrite_msg.setVisible(false);
                        btn_ramwrite_clean.setVisible(false);
                    }
                }           
            });
            Rbutton_RAN_Cell_cng_comm.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {     
                    if (e.getStateChange() == 1)
                    {
                        textBox_ram_write_clean.setVisible(true);
                        label_cleanwrite_msg.setVisible(true);
                        btn_ramwrite_clean.setVisible(true);
                    }
                    else
                    {
                        textBox_ram_write_clean.setVisible(false);
                        label_cleanwrite_msg.setVisible(false);
                        btn_ramwrite_clean.setVisible(false);
                    }
                }           
             });




//======================================================
//          Добавление элементов на форму

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
            container.add(button_1cell);
            container.add(button_runALL);
            container.add(checkBox_writetoALU);
            container.add(Rbutton_RAN_Cell_cng_clean);
            container.add(Rbutton_RAN_Cell_cng_comm);
            container.add(Rbutton_RAN_Cell_cng_data);
            container.add(label_RAM_in);
            container.add(textBox_ram_write_clean);
            container.add(label_cleanwrite_msg);
            container.add(btn_ramwrite_clean);
            
            refreshUI();                 
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

        //КНОПКА ВЫПОЛНЕНИЯ ОДНОЙ ЯЧЕЙКИ
        class Button_1cell_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if(UU.CANT == MEM - 1)
                {
                    JOptionPane.showMessageDialog(null, "Достигнута последняя ячейка", "Конец", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                UU.RC = RAM.get_cell(UU.CANT);
                if (compute() == 666)
                    JOptionPane.showMessageDialog(null, "Встречена команда завершения", "Конец", JOptionPane.PLAIN_MESSAGE);
                refreshUI();
            }
        }

        //КНОПКА ВЫПОЛНЕНИЯ ВСЕЙ ПРОГРАММЫ
        class Button_runALL_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {

                while(true)
                {
                    UU.RC = RAM.get_cell(UU.CANT);
                    if (compute() == 666)
                    {
                        JOptionPane.showMessageDialog(null, "Встречена команда завершения", "Конец", JOptionPane.PLAIN_MESSAGE);
                        break;
                    }
                    if (UU.CANT == MEM)
                    {
                        UU.CANT--;
                        break;
                    }
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
            if (UU.CANT < 10)
                textBox_CANT.setText("[00" + UU.CANT + "]");
            else if(UU.CANT < 100)
                textBox_CANT.setText("[0" + UU.CANT + "]");
            else
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
            RAM_choser.setValue(UU.CANT);
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

