PCaPP
=====
Добавление поддержки русского языка xml-файлам из PerMa Compatibility and PaMa Patches (PCaPP).
Перевода требуют все связки (binding) вида identifier + substring, переводится только подстрока substring, английский вариант оставляется. Необходимы два варианта перевода: с заглавной и строчной первой буквами, чтобы не указывать все склонения пишем часть слова. При добавлении поддержки какого-либо мода, которого нет в оригинальном PCaPP - дописываем нужные строки в конец соответствующего файла + строку с комментарием, в котором указано название мода.

Пример
------

**Было:**

    <binding>
		<identifier>Daedric</identifier>
		<substring>Daedric</substring>
	</binding>

**Стало:**

    <binding>
		<identifier>Daedric</identifier>
		<substring>Daedric</substring>
	</binding>
	<binding>
		<identifier>Daedric</identifier>
		<substring>Даэдрич</substring>
	</binding>
	<binding>
		<identifier>Daedric</identifier>
		<substring>даэдрич</substring>
	</binding>


----------
**Поддерживаемые моды:**

 - Skyrim
 -  
