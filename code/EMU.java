import java.util.BitSet;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class EMU extends secondary{  

    static class UI extends JFrame {
        //кнопки 
        private JButton button_set_CANT = new JButton("<html><div align='center'>Установить cчётчик</div></html>"); 
        private JButton button_1cell = new JButton("<html><div align='center'>Выполнить текущую ячейку</div></html>"); 
        private JButton button_runALL = new JButton("<html><div align='center'>Выполнить программу</div></html>");
        private JCheckBox checkBox_runSlow = new JCheckBox("Медленно", false);

        private JButton button_clearRAM = new JButton("<html><div align='center'>Очистить память</div></html>");
        private JButton button_fillRAM = new JButton("<html><div align='center'>Считать файл памяти</div></html>");
        private JButton button_dumpRAM = new JButton("<html><div align='center'>Дамп памяти</div></html>");       

        //поле вывода СЧАК
        private JLabel label_CANT_out = new JLabel("СЧАК");
        private JLabel label_CURcell_out = new JLabel("Ячейка на исполнение");
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
        private JLabel label_RAM_out_com_MEM = new JLabel();
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
        private JCheckBox checkBox_writetoALU = new JCheckBox("Запись в регистр АЛУ", false);

        //чистая запись
        private JTextField textBox_ram_write_clean = new JTextField();
        private JLabel label_cleanwrite_msg = new JLabel("Битовый набор без пробелов (32bit)");
        private JButton button_ramwrite_clean = new JButton("Ввод"); 

        //запись команды
        private JTextField textBox_ram_write_comm_c = new JTextField();
        private JSpinner spiner_ram_write_com_addr = new JSpinner(new SpinnerNumberModel(0, 0, MEM - 1, 1));
        private JLabel label_comwrite_msg = new JLabel("Команда; адрес операнда");
        private JButton button_ramwrite_coms = new JButton("Ввод"); 

        //запись данных
        private JTextField textBox_ram_write_data = new JTextField();
        private JLabel label_datawrite_msg = new JLabel("Десятичное число");
        private JButton button_ramwrite_data = new JButton("Ввод"); 
        private JRadioButton rBut_type_int = new JRadioButton("integer");
        private JRadioButton rBut_type_float = new JRadioButton("float");
        
        public UI() {
            super("Учебный эмулятор ЭВМ " + VER);
            this.setIconImage(Toolkit.getDefaultToolkit().getImage("emu.png"));
            this.setBounds(100,100,900,675);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Container container = this.getContentPane();
            container.setLayout(null);

//======================================================
//          Вывод активной ячейки и регистра АЛУ

            int baseX = 10;    //базовые координаты блока
            int baseY = 0;
            label_CANT_out.setBounds(baseX, baseY, 75, 55);
            textBox_CANT.setBounds(baseX,baseY + 40, 60, 25);
            label_CURcell_out.setBounds(baseX + 75, baseY, 365, 55);
            textBox_CURcell.setBounds(baseX + 70,baseY + 40, 300, 25);
        
            baseX = 10;
            baseY = 60;
            label_ALU_out.setBounds(baseX + 75, baseY, 165, 55);
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
            list_RAM_tmp.addListSelectionListener(new listRAM_Listener());
            list_RAM_final.setBounds(baseX, baseY + 40 , 370, 400);


//======================================================
//          Кнопки очистки памяти, заполнения из файла

            baseX = 10;
            baseY = 565;
            button_clearRAM.setBounds(baseX, baseY, 110, 65);
            button_clearRAM.addActionListener(new Button_clearRAM_EventListener());

            button_fillRAM.setBounds(baseX + 120, baseY, 110, 65);
            button_fillRAM.addActionListener(new Button_fillRAM_EventListener());

            button_dumpRAM.setBounds(baseX + 240, baseY, 110, 65);
            button_dumpRAM.addActionListener(new Button_dumpRAM_EventListener());


//======================================================
//          Выборщик ячейки

            baseX = 400;
            baseY = 15;
            label_RAM_chooser.setBounds(baseX, baseY, 100, 25);
            RAM_choser.setBounds(baseX + 5, baseY + 25, 75, 25);
            RAM_choser.addChangeListener(new RAM_chooser_Listener());


//======================================================
//          Кнопки выставления CANT, пошаговой работы, полной работы

            baseX = 510;
            baseY = 15;
            button_set_CANT.setBounds(baseX, baseY, 110, 65);
            button_set_CANT.addActionListener(new Button_SETCANT_EventListener());

            button_1cell.setBounds(baseX + 120, baseY, 110, 65);
            button_1cell.addActionListener(new Button_1cell_EventListener());

            button_runALL.setBounds(baseX + 240, baseY, 110, 65);
            button_runALL.addActionListener(new Button_runALL_EventListener());

            checkBox_runSlow.setBounds(baseX + 250, baseY + 65, 100, 25);
            

//======================================================
//          Вывод значения выбранной ячейки

            baseX = 400;
            baseY = 300;
            label_RAM_out.setBounds(baseX, baseY, 480, 25);

            label_RAM_out_com.setBounds(baseX + 30, baseY + 30, 80, 25);
            label_RAM_out_com_MEM.setBounds(baseX + 170, baseY + 5, 80, 25);
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
            Rbutton_RAN_Cell_cng_clean.setBounds(baseX + 20, baseY + 30, 120,25);
            Rbutton_RAN_Cell_cng_comm.setBounds(baseX + 20, baseY + 60, 120,25);
            Rbutton_RAN_Cell_cng_data.setBounds(baseX + 20, baseY + 90, 120,25);
            checkBox_writetoALU.setBounds(baseX + 20, baseY + 120, 200, 25);


            //поля чистого ввода
            textBox_ram_write_clean.setBounds(baseX + 140, baseY + 30, 300, 25);
            label_cleanwrite_msg.setBounds(baseX + 180, baseY + 55, 260, 25);
            button_ramwrite_clean.setBounds(baseX + 240, baseY + 80, 80, 25);
            button_ramwrite_clean.addActionListener(new button_ramwrite_clean_EventListener());


            //поля ввода команды
            textBox_ram_write_comm_c.setBounds(baseX + 180, baseY + 30, 150, 25);
            spiner_ram_write_com_addr.setBounds(baseX + 340, baseY + 30, 60, 25);
            label_comwrite_msg.setBounds(baseX + 220, baseY + 55, 260, 25);
            button_ramwrite_coms.setBounds(baseX + 240, baseY + 80, 80, 25);
            button_ramwrite_coms.addActionListener(new button_ramwrite_coms_EventListener());

            //поля ввода данных
            textBox_ram_write_data.setBounds(baseX + 180, baseY + 30, 170, 25);
            label_datawrite_msg.setBounds(baseX + 220, baseY + 55, 200, 25);
            rBut_type_int.setBounds(baseX + 360, baseY + 30, 80, 20);
            rBut_type_float.setBounds(baseX + 360, baseY + 60, 80, 20);
            button_ramwrite_data.setBounds(baseX + 240, baseY + 80, 80, 25);
            button_ramwrite_data.addActionListener(new button_ramwrite_data_EventListener());


            //радиокнопки ввода
            ButtonGroup group_Rbutton_RAM_in = new ButtonGroup();
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_clean);
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_comm);
            group_Rbutton_RAM_in.add(Rbutton_RAN_Cell_cng_data);
            Rbutton_RAN_Cell_cng_clean.setSelected(true);

            ButtonGroup group_Rbutton_RAM_in_data = new ButtonGroup();
            group_Rbutton_RAM_in_data.add(rBut_type_int);
            group_Rbutton_RAM_in_data.add(rBut_type_float);
            rBut_type_int.setSelected(true);

            //обработчики событий радиокнопок ввода
            Rbutton_RAN_Cell_cng_clean.addItemListener(new rButton_ram_clean_Listener());
            Rbutton_RAN_Cell_cng_comm.addItemListener(new rButton_ram_com_Listener());
            Rbutton_RAN_Cell_cng_data.addItemListener(new rButton_ram_data_Listener()); 


    
            //Спрятать поля ввода кроме чистого при запуске
            textBox_ram_write_comm_c.setVisible(false);
            spiner_ram_write_com_addr.setVisible(false);
            label_comwrite_msg.setVisible(false);
            button_ramwrite_coms.setVisible(false);

            textBox_ram_write_data.setVisible(false);
            label_datawrite_msg.setVisible(false);
            button_ramwrite_data.setVisible(false);
            rBut_type_int.setVisible(false);
            rBut_type_float.setVisible(false);


//======================================================
//          Добавление элементов на форму

            container.add(label_ALU_out);
            container.add(textBox_RO);
            container.add(textBox_ALU);
            container.add(label_CANT_out);
            container.add(label_CURcell_out);
            container.add(textBox_CANT);
            container.add(textBox_CURcell);
            container.add(label_RAM_list);
            container.add(list_RAM_final);
            container.add(label_RAM_out);
            container.add(label_RAM_out_com);
            container.add(label_RAM_out_com_MEM);
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
            //container.add(checkBox_runSlow);
            container.add(checkBox_writetoALU);
            container.add(Rbutton_RAN_Cell_cng_clean);
            container.add(Rbutton_RAN_Cell_cng_comm);
            container.add(Rbutton_RAN_Cell_cng_data);
            container.add(label_RAM_in);
            container.add(textBox_ram_write_clean);
            container.add(label_cleanwrite_msg);
            container.add(button_ramwrite_clean);
            container.add(textBox_ram_write_comm_c);
            container.add(spiner_ram_write_com_addr);
            container.add(label_comwrite_msg);
            container.add(button_ramwrite_coms);
            container.add(textBox_ram_write_data);
            container.add(label_datawrite_msg);
            container.add(button_ramwrite_data);
            container.add(rBut_type_int);
            container.add(rBut_type_float);
            container.add(button_clearRAM);
            container.add(button_fillRAM);
            container.add(button_dumpRAM);
            refreshUI();                 
        }
        

        //КНОПКА ОЧИСТКИ ПАМЯТИ
        class Button_clearRAM_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                RAM.zero();
                ALU.clearRO();
                refreshUI();
                refresh_RAM_out();
            }
        }

        //КНОПКА ЗАПОЛНЕНИЯ ПАМЯТИ ИЗ ФАЙЛА
        class Button_fillRAM_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fill_fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Текстовые файлы", "txt");
                fill_fileChooser.setFileFilter(filter);
                fill_fileChooser.setCurrentDirectory(new File(Paths.get("").toAbsolutePath().toString()));
                int ret = fill_fileChooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION){
                    try
                    {
                        File fillRAM_file = fill_fileChooser.getSelectedFile();
                        FileReader reader = new FileReader(fillRAM_file);
                        BufferedReader file_RAMreader = new BufferedReader(reader);
                        RAM.zero();
                        int cnt_good = 0;
                        int cnt_all = 0;
                        String line = file_RAMreader.readLine();
                        while (line != null){
                            if (RAM.file_RAMfill(cnt_all, line))
                                cnt_good++;
                            cnt_all++;
                            line = file_RAMreader.readLine();
                        }
                        String resultmessage = "Внесено ячеек " + (cnt_good);
                        if (cnt_all - cnt_good > 0)
                            resultmessage += "\nПроигнорированы записи с посторонними символами ("+(cnt_all - cnt_good)+")";
                        MessageBox(resultmessage);
                        file_RAMreader.close();
                        refreshUI();
                        refresh_RAM_out();
                    }
                    catch (FileNotFoundException t){
                        MessageBox("Файл не найден");
                    }
                    catch (IOException t){
                        MessageBox("Ошибка взаимодействия");
                    }
                }
            }
        }

        //КНОПКА ДАМПА ПАМЯТИ
        class Button_dumpRAM_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JFileChooser dump_fileChooser = new JFileChooser();
                dump_fileChooser.setCurrentDirectory(new File(Paths.get("").toAbsolutePath().toString()));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Текстовые файлы", "txt");
                dump_fileChooser.setFileFilter(filter);
                //dump_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int ret = dump_fileChooser.showDialog(null, "Выбрать файл для дампа");
                if (ret == JFileChooser.APPROVE_OPTION){
                    try
                    {
                        FileWriter writer = new FileWriter(dump_fileChooser.getSelectedFile());
                        for (int i = 0; i < MEM; i++)
                            writer.write(RAM.show_cell(i) + "\n");
                        writer.close();
                        MessageBox("Дамп сделан в файл '" + dump_fileChooser.getSelectedFile().getName() + "'");
                    }
                    catch (IOException t){
                        MessageBox("Не удалось создать файл");
                    }
                }
            }
        }


        //КНОПКА ВЫСТАВЛЕНИЯ СЧАК
        class Button_SETCANT_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {                 
                try{
                    UU.CANT = (int)RAM_choser.getValue();
                }
                catch (Throwable t){
                    MessageBox("Ошибка ввода счётчика");
                }
                refreshUI();
            }
        }

        //КНОПКА ВЫПОЛНЕНИЯ ОДНОЙ ЯЧЕЙКИ
        class Button_1cell_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if(UU.CANT == MEM - 1){
                    MessageBox("Достигнута последняя ячейка");
                    return;
                }
                UU.RC = RAM.get_cell(UU.CANT);
                if (compute() == 666)
                    MessageBox("Встречена команда завершения");
                refreshUI();
                list_RAM_tmp.setSelectedIndex(UU.CANT);
            }
        }

        //КНОПКА ВЫПОЛНЕНИЯ ВСЕЙ ПРОГРАММЫ
        class Button_runALL_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                button_runALL.setVisible(false);
                checkBox_runSlow.setEnabled(false);
                greyButtons(false);
                while(true)
                {
                    UU.RC = RAM.get_cell(UU.CANT);
                    if(UU.CANT == MEM - 1){
                        MessageBox("Достигнута последняя ячейка");
                        break;
                    }
                    if (compute() == 666)
                    {
                        MessageBox("Встречена команда завершения");
                        break;
                    }
                    if (UU.CANT == MEM)
                    {
                        UU.CANT--;
                        break;
                    }
                    list_RAM_tmp.setSelectedIndex(UU.CANT);
                }
                refreshUI();
                
                button_runALL.setVisible(true);
                checkBox_runSlow.setEnabled(true);
                greyButtons(true);
            }
        }

        //КНОПКА ЧИСТОГО ВВОДА
        class button_ramwrite_clean_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                int ad = (int)RAM_choser.getValue(); //запомнить адрес для записи
                String bits = textBox_ram_write_clean.getText();
                int len = bits.length();
                if (len > CELL)
                {
                    MessageBox(len + "bits; Переполнение");
                    return;
                }
                if (len < CELL) // удлинение строки при необходимости
                {
                    String temp = "";
                    for (int i = 0; i < CELL - len; i++)
                        temp += "0";
                    bits = temp + bits;
                }
                BitSet WRbit = new BitSet(CELL);
                boolean fail = false;
                for (int i = 0; i < CELL; i++)
                {
                    if (bits.charAt(i) == '1')
                        WRbit.set(CELL - 1 - i);
                    else if (bits.charAt(i) == '0')
                        WRbit.clear(CELL - 1 - i);
                    else
                    {
                        MessageBox("Неверный символ в наборе!");
                        WRbit.clear();
                        fail = true;
                        break;
                    }
                }
                if (!fail)
                {
                    if (checkBox_writetoALU.isSelected())
                        ALU.write_RO(WRbit);
                    else
                        RAM.write_cell(ad, WRbit);
                }
                refreshUI();
                refresh_RAM_out();
            }
        }


        //КНОПКА ВВОДА КОМАНДЫ
        class button_ramwrite_coms_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                int ad = (int)RAM_choser.getValue(); //запомнить адрес для записи
                int oper = (int)spiner_ram_write_com_addr.getValue(); //адрес операнда в команде
                int CommandCode; //код команды
                boolean fail = false;
                try
                {   //попытка считать число
                    CommandCode =  Integer.parseInt(textBox_ram_write_comm_c.getText());
                }
                catch (NumberFormatException nfe)
                {
                    //попытка расшифровать мнемонику
                    String Stemp = textBox_ram_write_comm_c.getText();
                    CommandCode = decoder(Stemp);
                    if (CommandCode == 0)
                    {
                        MessageBox("Команда не распознана");
                        fail = true;
                    }
                }
                if (!fail)
                {
                    BitSet data = new BitSet(CELL);
                    if (CommandCode < 0)
                        data = make_one(0, oper);
                    else 
                        data = make_one(CommandCode, oper);

                    if (checkBox_writetoALU.isSelected())
                        ALU.write_RO(data);
                    else
                        RAM.write_cell(ad, data);
                }
                refreshUI();
                refresh_RAM_out();
            }
        }

        //КНОПКА ВВОДА ДАННЫХ
        class button_ramwrite_data_EventListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                int temp;
                float ftemp;

                int ad = (int)RAM_choser.getValue();
                boolean fail = false;
                if (rBut_type_int.isSelected())
                {
                    try
                    {   //попытка считать число
                        temp =  Integer.parseInt(textBox_ram_write_data.getText());
                    }
                    catch (NumberFormatException nfe)
                    {
                        fail = true;
                        temp = 0;
                        MessageBox("Неверный ввод или переполнение");
                    }
                    if (!fail)
                    {
                        BitSet data = new BitSet(CELL);
                        data = int_to_bit(temp);
                        if (checkBox_writetoALU.isSelected())
                            ALU.write_RO(data);
                        else
                            RAM.write_cell(ad, data);
                    }
                }
                else if (rBut_type_float.isSelected())
                {
                    try
                    {   //попытка считать число
                        ftemp =  Float.parseFloat(textBox_ram_write_data.getText());
                    }
                    catch (NumberFormatException nfe)
                    {
                        String tmpF = textBox_ram_write_data.getText();
                        try
                        {
                            ftemp = Float.parseFloat(tmpF.replace(",", "."));
                        }
                        catch (NumberFormatException nfe1)
                        {
                            fail = true;
                            ftemp = (float)0.0;
                            MessageBox("Неверный ввод");
                        }
                    }
                    if ((ftemp > 3.40282346639e+38) || (ftemp < -3.40282346639e+38))
                    {
                        MessageBox("Переполнение");
                        fail = true;
                    }
                    if (!fail)
                    {
                        BitSet data = new BitSet(CELL);
                        data = float_to_bit(ftemp);
                        if (checkBox_writetoALU.isSelected())
                            ALU.write_RO(data);
                        else
                            RAM.write_cell(ad, data);
                    }
                }
                else
                    MessageBox("Что-то сильно не так...");
                refreshUI();
                refresh_RAM_out();
            }
        }


        //СОБЫТИЕ ВЫБОРА ЯЧЕЙКИ СПИНЕРОМ (выделеняет ячейку в списке)
        class RAM_chooser_Listener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
               list_RAM_tmp.setSelectedIndex((int)RAM_choser.getValue());
               refresh_RAM_out();
            }
        }

        //ИЗМЕНЕНИЕ ВЫБРАННОЙ ЯЧЕЙКИ В СПИСКЕ RAM
        class listRAM_Listener implements ListSelectionListener{ 
            public void valueChanged(ListSelectionEvent e){
                RAM_choser.setValue(list_RAM_tmp.getSelectedIndex());
            }
        }

        //ОБРАБОТЧИКИ РАДИОКНОПОК СКРЫТИЯ ПОЛЕЙ ВВОДА 
        class rButton_ram_clean_Listener implements ItemListener{
            public void itemStateChanged(ItemEvent e) {     
                if (e.getStateChange() == 1)
                {
                    textBox_ram_write_clean.setVisible(true);
                    label_cleanwrite_msg.setVisible(true);
                    button_ramwrite_clean.setVisible(true);
                }
                else
                {
                    textBox_ram_write_clean.setVisible(false);
                    label_cleanwrite_msg.setVisible(false);
                    button_ramwrite_clean.setVisible(false);
                }
            }           
        }
        class rButton_ram_com_Listener implements ItemListener{
            public void itemStateChanged(ItemEvent e) {     
                if (e.getStateChange() == 1)
                {
                    textBox_ram_write_comm_c.setVisible(true);
                    spiner_ram_write_com_addr.setVisible(true);
                    label_comwrite_msg.setVisible(true);
                    button_ramwrite_coms.setVisible(true);
                }
                else
                {
                    textBox_ram_write_comm_c.setVisible(false);
                    spiner_ram_write_com_addr.setVisible(false);
                    label_comwrite_msg.setVisible(false);
                    button_ramwrite_coms.setVisible(false);
                }
            }           
        }
        class rButton_ram_data_Listener implements ItemListener{
            public void itemStateChanged(ItemEvent e) {     
                if (e.getStateChange() == 1)
                {
                    textBox_ram_write_data.setVisible(true);
                    label_datawrite_msg.setVisible(true);
                    button_ramwrite_data.setVisible(true);
                    rBut_type_int.setVisible(true);
                    rBut_type_float.setVisible(true);
                }
                else
                {
                    textBox_ram_write_data.setVisible(false);
                    label_datawrite_msg.setVisible(false);
                    button_ramwrite_data.setVisible(false);
                    rBut_type_int.setVisible(false);
                    rBut_type_float.setVisible(false);
                }
            }            
        }
        
        //ПЕРЕЗАГРУЗКА UI
        private void refreshUI(){
            int zerosCant = (UU.CANT == 0) ? 1 : (int)Math.ceil(Math.log10(UU.CANT + 1));
            textBox_CANT.setText(makeIndex(UU.CANT, MEMzeros - zerosCant));
            textBox_CURcell.setText(RAM.show_cell(UU.CANT));
            textBox_ALU.setText(ALU.showRO());
            boolean first = false;
            if (listRAM.isEmpty())
                first = true;
            String S = "";
            int zerosI = 1;
            int rankCheker = 10;
            for (int i = 0; i < MEM; i++)
            {
                if (i == rankCheker)
                {
                    zerosI++;
                    rankCheker *= 10;
                }
                S = makeIndex(i, MEMzeros - zerosI) + ": " + RAM.show_cell(i);
                if (first)
                    listRAM.addElement(S);
                else
                    listRAM.set(i,S);
            }
        }
        
        //ПЕРЕЗАГРУЗКА ПОЛЕЙ ДИНАМИЧЕСКОГО ВЫВОД ЯЧЕЙКИ
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
            label_RAM_out_com_MEM.setText(decoder(C));
        }

        private void greyButtons(boolean state)
        {
            button_1cell.setEnabled(state);
            button_clearRAM.setEnabled(state);
            button_dumpRAM.setEnabled(state);
            button_fillRAM.setEnabled(state);
            button_ramwrite_clean.setEnabled(state);
            button_ramwrite_coms.setEnabled(state);
            button_ramwrite_data.setEnabled(state);
            button_runALL.setEnabled(state);
            button_set_CANT.setEnabled(state);
        }
        
    }
    public static void MessageBox(String data){
        JOptionPane.showMessageDialog(null, data, "", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
		UI app = new UI();
		app.setVisible(true);
	}
}

