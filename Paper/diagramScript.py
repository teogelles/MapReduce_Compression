from matplotlib import pyplot
import numpy as np

"""
Grading.tar compression
"""

width = 50
left = [x*(width+10)+10 for x in range(9)]
height = [33.951, 35.890, 48.585, 166.798, 398.649, 67.372, 39.073, 51.881, 60.859]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(9)
pyplot.xticks(ind*(width+10)+width/2.+10, ('Hadoop 50MB', 'Hadoop 10MB', 'pbzip2 on sage', 'Hadoop 1MB', 'pbzip2 on pimento', 'gzip on ostrich', 'pbzip2 on ostrich', 'bzip2 on ostrich', 'bzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
Grading.tar decompression
"""

width = 50
left = [x*(width+10)+10 for x in range(8)]
height = [75.370, 62.072, 89.642, 42.988, 30.675, 39.948, 39.395, 34.599]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Decompression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(8)
pyplot.xticks(ind*(width+10)+width/2.+10, ('pbzip2 on sage', 'pbzip2 with Hadoop compression on sage', 'pbzip2 on pimento', 'gzip on ostrich', 'pbzip2 on ostrich', 'bzip2 on ostrich', 'Hadoop', 'bzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
ADNI_Images.tar Compression
"""

width = 50
left = [x*(width+10)+10 for x in range(9)]
height = [115.514, 104.635, 249.504, 624.306, 1306.381, 228.302, 191.084, 117.438, 189.190]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(9)
pyplot.xticks(ind*(width+10)+width/2.+10, ('Hadoop 10MB', 'Hadoop 50MB', 'pbzip2 on pimento', 'gzip on ostrich', 'pbzip2 on sage', 'pbzip2 on ostrich', 'Hadoop 1MB', 'bzip2 on ostrich', 'bzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
ADNI_Images.tar Decompression
"""

width = 50
left = [x*(width+10)+10 for x in range(8)]
height = [578.976, 255.385, 331.582, 212.308, 237.825, 204.408, 234.985, 248.999]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Decompression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(8)
pyplot.xticks(ind*(width+10)+width/2.+10, ('pbzip2 on pimento', 'pbzip2 on ostrich', 'gzip on ostrich', 'pbzip2 on sage', 'pbzip2 with our compression on sage', 'bzip2 on ostrich', 'bzip2 on sage', 'Hadoop'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
MacTeX.pkg Compression
"""

width = 50
left = [x*(width+10)+10 for x in range(9)]
height = [94.571, 106.411, 118.451, 351.064, 809.559, 124.970, 90.631, 109.264, 84.339]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(9)
pyplot.xticks(ind*(width+10)+width/2.+10, ('gzip on ostrich', 'pbzip2 on sage', 'Hadoop 50MB', 'Hadoop 10MB', 'pbzip2 on pimento', 'Hadoop 1MB', 'pbzip2 on ostrich', 'bzip2 on ostrich', 'bzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
MacTeX.pkg Decompression
"""

width = 50
left = [x*(width+10)+10 for x in range(8)]
height = [109.292, 161.472, 306.307, 68.898, 55.311, 70.078, 59.089, 51.517]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Decompression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(8)
pyplot.xticks(ind*(width+10)+width/2.+10, ('pbzip2 with our compression on sage', 'pbzip2 on sage', 'gzip on ostrich', 'pbzip2 on ostrich', 'pbzip2 on pimento', 'Hadoop', 'bzip2 on ostrich', 'bzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()



"""
grading.tar Compression Size
"""

width = 50
left = [x*(width+10)+10 for x in range(6)]
height = [439123795, 439542111, 409521650, 409956769, 467021531, 1662914560]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Size (bytes)", fontsize=25)
ind = np.arange(6)
pyplot.xticks(ind*(width+10)+width/2.+10, ('bzip2', 'pbzip2', 'Hadoop 50MB / 10MB', 'Hadoop 1MB', 'gzip', 'Original'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()

"""
ADNI-Images.tar Compression Size
"""

width = 50
left = [x*(width+10)+10 for x in range(6)]
height = [1395454769, 1398039578, 1378603426, 1385427160, 1575019702, 15819407360]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Size (bytes)", fontsize=25)
ind = np.arange(6)
pyplot.xticks(ind*(width+10)+width/2.+10, ('bzip2', 'pbzip2', 'Hadoop 50MB / 10MB', 'Hadoop 1MB', 'gzip', 'Original'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
MacTeX.pkg Compression Size
"""

width = 50
left = [x*(width+10)+10 for x in range(6)]
height = [2512999211, 2467408782, 2467719810, 2454570219, 2454716221, 2444004124]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Size (bytes)", fontsize=25)
ind = np.arange(6)
pyplot.xticks(ind*(width+10)+width/2.+10, ('bzip2', 'pbzip2', 'gzip', 'Hadoop 50MB / 10MB', 'Hadoop 1MB', 'Original'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()


"""
BIGADNI.tar Compression
"""

width = 50
left = [x*(width+10)+10 for x in range(3)]
height = [556.492, 801.801, 1107.361]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Time (s)", fontsize=25)
ind = np.arange(3)
pyplot.xticks(ind*(width+10)+width/2.+10, ('Hadoop 50MB', 'pbzip2 on pimento', 'pbzip2 on sage'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()

"""
BIGADNI.tar Compression Size
"""

width = 50
left = [x*(width+10)+10 for x in range(3)]
height = [5581870044, 5541666828, 63277639680]
height.sort()


pyplot.bar(left, height, width)
pyplot.xlabel("Compression Algorithm Used", fontsize=25)
pyplot.ylabel("Size (bytes)", fontsize=25)
ind = np.arange(3)
pyplot.xticks(ind*(width+10)+width/2.+10, ('pbzip2', 'Hadoop 50MB', 'Original'))
pyplot.xlim(0, max(left)+width+10)
pyplot.show()
