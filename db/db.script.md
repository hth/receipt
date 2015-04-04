#### Date Apr 5 2015
db.getCollection('BIZ_STORE').update({}, {$unset : {'LAT': '', 'LNG': ''}}, false, true)

#### Date Mar 28 2015
db.getCollection('ITEM').update({'TT' : 'NOT_TAXED'}, {$set : {'TT' : 'NT'}}, false, true)
db.getCollection('ITEM').update({'TT' : 'TAXED'}, {$set : {'TT' : 'T'}}, false, true)
db.getCollection('ITEM_OCR').update({'TT' : 'NOT_TAXED'}, {$set : {'TT' : 'NT'}}, false, true)
db.getCollection('ITEM_OCR').update({'TT' : 'TAXED'}, {$set : {'TT' : 'T'}}, false, true)