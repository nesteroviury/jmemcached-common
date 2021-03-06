# jmemcached-common
<h5>Описание протокола</h5>
- протокол бинарный
- пакеты запроса и ответа имеют динамическую длину 
- ключ - строка в кодировке ASCII(каждый символ кодируется 1 байтом), объект - любой Java объект
- пакет запроса
  - ver - байт версии(3 бита для старшего значения, 4 бита для младшего). Текущее значение 16(1.0), максимальная версия 7.15
  - cmd - байт команды, максимальное количество команд = 256
  - flg - байт флагов, определяет, есть ли дальше данные; использует побитовую кодировку, максимальное количество флагов = 8
  - key bytes - байты строкового ключа
  - kl - длина ключа, размер = 1 байт, максимальное значение длины ключа = 127 байт
  - data bytes - массив байтов сериализованного Java объекта
  - dl - длина массива байтовб размер 4 байта(int), максимальный размер данных = 2 Gb
  - ttl - время устаревания объекта по ключу, размер 8 байтов(long)
  - команды
    - clear: ver, cmd, flg
    - get, remove: ver, cmd, flg, kl, key bytes
    - put:  ver, cmd, flg, kl, key bytes, dl(4b), data bytes
    - put:  ver, cmd, flg, kl, key bytes, ttl(8b), dl(4b), data bytes
  - флаги и длина пакета
    - flg = 0000 0000 - пакет завершился, в потоке нет данны (команда clear)
    - flg = 0000 0001 - указывает, что после флагов будут байты длины ключа и ключа (команды get, remove)
    - flg = 0000 0101 - указывает, что после флагов будут байты длины ключа, ключа, длины данных, данных (команда put без ttl)
    - flg = 0000 0111 - указывает, что после флагов будут байты длины ключа, ключа, ttl, длины данных, данных (команда put с ttl)
  - размеры пактов
    - минимальный = 3 байта (команда clear)
    - максимальный = 2 GB (команда put с ttl)
    
- пакет ответа
  - ver - байт версии(3 бита для старшего значения, 4 бита для младшего). Текущее значение 16(1.0), максимальная версия 7.15
  - sts - байт статуса команды (максимальное количество статусов = 256)
  - dat - байт наличия данных в пакете ответа, 0 - данные отсутствуют, 1 - присутствуют
  - dl - длина массива байтовб размер 4 байта(int), максимальный размер данных = 2 Gb
  - data bytes - массив байтов сериализованного Java объекта
  - empty: ver, sts, dat
  - with data: ver, sts, dat, dl, data bytes
  - размеры пактов
    - минимальный = 3 байта (команда clear)
    - максимальный = 2 GB (команда put с ttl)