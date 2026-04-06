all : TEXTS


######################################################################

TEXTS : select-prices.pdf


######################################################################
# TEXTS

select-prices.pdf : select-prices.md
	pandoc -f markdown -t pdf -V geometry:a4paper -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
