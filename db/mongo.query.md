## Using And with multiple OR with showing selected field    
    
    db.getCollection('RECEIPT').find(    
        {
            $and : [
                {$or : [ {"_id" : new ObjectId("56c53296939200f6d6b6661b")}, {"RF" : "56c53296939200f6d6b6661b"}, {"A" : true}]},
                {$or : [ {"A" : true}]}
            ]}, 
        {
            "SC" : 1, "A" : 1, "RID" : 1
        }
    )