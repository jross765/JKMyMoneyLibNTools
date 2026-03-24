all : TEXTS


######################################################################

TEXTS : select-prices.pdf


######################################################################
# TEXTS

select-prices.pdf : select-prices.md \
                    ../xsec/prcid-logic.png
	pandoc -f markdown -t pdf -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
