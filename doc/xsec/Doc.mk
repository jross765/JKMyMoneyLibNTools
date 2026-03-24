all : PICS


######################################################################

PICS : prcid-logic.png


######################################################################
# PICS

prcid-logic.png : prcid-logic.pdf

prcid-logic.pdf : prcid-logic.odg


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
