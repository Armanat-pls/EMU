#ifndef CL_H
#define CL_H
#include <iostream>
#include <fstream>
#include <bitset>
#define CELL 32	//Размер ячейки в бит
#define MEM 256 // Количество ячеек памяти
#define BMEM 8	// Количество бит на адрес памяти
#define VER "1.5.3"
using std::bitset;

class BRAIN
{	
	bitset<CELL> RO; //Аккумулятор, на одну ячейку
	public:
		BRAIN();		//КОНСТРУКТОР
		void write_RO(bitset<CELL> data);
		bitset<CELL> get_RO();
		void showRO();		//вывод регистра
};

class MEMORY
{	
	private:
		bitset<CELL> TABLE[MEM];	// ЯЧЕЙКИ ПАМЯТИ [адрес ячейки][данные] bitset индексирует справа налево, требуются развороты при работе с индексами
	public:
		MEMORY();				//КОНСТРУКТОР

		void write_cell();								//Запись ячейки памяти вручную
		void write_cell(short addr, bitset<CELL> data); //Запись ячейки памяти
		bitset<CELL> get_cell(short addr);				//Получить данные из памяти

		
		void fill_ALL();	//СЧИТАТЬ RAM ИЗ ФАЙЛА
		void show();	//ВЫВОД ВСЕЙ(или зоны) RAM НА ЭКРАН
		void show_cell(short addr);//ВЫВОД ОДНОЙ ЯЧЕЙКИ
		void dump_ALL();	//ВЫВОД ВСЕЙ RAM В ФАЙЛ
		void zero(); 	// Обнуление памяти

		
		
};
class CONTROL
{
	public:
		short CANT;			// СЧЁТЧИК АДРЕСА КОМАНД
		bitset<CELL> RC;	// РЕГИСТР АКТИВНОЙ ЯЧЕЙКИ
		CONTROL();			//КОНСТРУКТОР

		//Разделить команду на значащие ячейки
		void cut_com(bitset<CELL-BMEM> &C, bitset<BMEM> &A);

};

class CPU
{
	private:
		BRAIN ALU;
		MEMORY RAM;
		CONTROL UU;	
		short compute(); //метод выполняющий команду
	public:
		void work();//Основная зона
		
	
};	
#endif
