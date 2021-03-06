#!/bin/bash
java -cp bin/JavaImageRetrieval.jar retrieval.indexer.main.RetrievalIndexerMain $1 $2 $3 $4 $5 $6 $7 $8


     # Param0: Server host
     # param1: Server port
     # Param2: Picture URI
     # Param3: (Optional) 'async' or 'sync' string
     # Param4: (Optional) Storage name 
     # Param5: (Optional) Picture id
     # Param6: (Optional) Picture properties keys (comma sep) (e.g. id,name,date)
     # Param7: (Optional) Picture properties values (comma sep) (e.g. 123,test,2014/10/31)
