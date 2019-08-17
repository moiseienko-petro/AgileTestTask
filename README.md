# How to run
 java -cp %RELATIVE_PATH%\AgileTestTask.jar com.agileengine.Driver <input_origin_file_path> <input_other_sample_file_path> <input_id_of_searched_element>
 or
 java -jar %RELATIVE_PATH%\AgileTestTask.jar <input_origin_file_path> <input_other_sample_file_path> <input_id_of_searched_element>
 <input_id_of_searched_element> - is optional - by default it equals make-everything-ok-button
 
# Result output
sample-1-evil-gemini.html :
[main] INFO com.agileengine.Driver - Path to element: html > body > div > div > div[2] > div > div > div[1] > a[1]
[main] INFO com.agileengine.Driver - 3 attributes are equal with original object

sample-2-container-and-clone.html :
[main] INFO com.agileengine.Driver - Path to element: html > body > div > div > div[2] > div > div > div[1] > div > a
[main] INFO com.agileengine.Driver - 3 attributes are equal with original object

sample-3-the-escape.html  :
[main] INFO com.agileengine.Driver - Path to element: html > body > div > div > div[2] > div > div > div[2] > a
[main] INFO com.agileengine.Driver - 4 attributes are equal with original object

sample-4-the-mash.html :
[main] INFO com.agileengine.Driver - Path to element: html > body > div > div > div[2] > div > div > div[2] > a
[main] INFO com.agileengine.Driver - 4 attributes are equal with original object

For not valid inputs: 
[main] WARN com.agileengine.JsoupFinder - Element is not present
