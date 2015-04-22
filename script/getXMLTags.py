# this script returns a txt file with the xml tags for the ideal points used in openDS 
# you need to provide the carData.txt file as input
# use from console: python getXMLTags.py carData.txt
from sys import argv
import string

script, filename = argv

newFile = open('xmlData.txt', 'w')
newFile.write("<geometries>")

idealTrack = "<idealTrack>\n"

l = 0
p = 0
with open(filename) as f:
	for line in f:
		
		l = l+1
		if l > 4:
			line = line.replace("\n", "")
			newFile.write("\n<point id='IdealPoint.")
			p = p+1
			idealTrack = idealTrack + "<point ref='IdealPoint." + str(p).zfill(4) + "'/>"
			newFile.write(str(p).zfill(4) + "'><translation><vector jtype='java_lang_Float' size='3'><entry>")
			for char in ':':
				line = line.replace(char, "</entry><entry>")
			
			newFile.write(line + "</entry></vector></translation></point>")



newFile.write("\n</geometries>\n\n\n")
newFile.write(idealTrack)
print "Done! The file xmlData.txt has been created for you."
