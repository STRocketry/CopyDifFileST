# CopyDifFileST
   CopyDifFileST моя первая самостоятельная программа на java.
Писалась с целью изучения некоторых библиотек (в частности Swing).
Что делает: ищет все файлы с нужным расширением в указанном каталоге (учитывая все вложенные каталоги) 
и копирует их в одну новую папку.
   Алгоритм использования:
1. В поле "Extension" ввести расширение без точки. Нажать Enter. Пример - mp3
2. В поле "From" ввести каталог поиска файлов. Нажать Enter. Пример - d:/Music
3. В поле "To" ввести папку куда копировать файлы. Нажать Enter. Пример - c:/Test
4. Нажать кнопку "Copy".
5. При необходимости отмены процесса нажать "Cancel". 
   После завершения копирования все поля очищаюстя и можно запуститьс новый процесс.
При вводе данных в поля и нажатия Enter цвет поля меняется.
К имени копируемого файла для удобства добовляется через двойное подчеркивание имя последнего каталога.
   Что планируется реализовать (не полный список) 19.06.15:
1. Разобраться с потоком, что бы не использовать метод stop().
2. Изменить иконку программы.
3...
