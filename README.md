# Bleichenbacher-Attack
Simulating Bleichenbacher Attack

This is a project with the finality of simulating Bleichenbacher Attack.
Developed using Java NetBeans 8.2, then changed to Apache NetBeans 9.0

#Running Project:
  
  -> Steps:
    
    1. Run Server File (shift + F6 || right click on File -> Run File)
    
    2. Run Enc_Window File (shift + F6 || right click on File -> Run File)
    
    -> Enc_Window gives encrypted messages and connects to server giving public key, n mode and encrypted message <-
    -> if Enc_Window is closed before getting an encrypted message, server needs to be closed and restarted if user wants the process to be done again <-
    
    3. After getting encrypted message, close Enc_Window and Run Client Window File
      -> Client Window sends s values automatically through a loop running 1000 times and runnning other 1000 more if user confirms <-
      
  -> NOTE:
    
    1. Windows running as client will throw IOException if server gets closed during the process and they will close connection
    
    2. To close connection client-server just need to write and send the word 'FINISH'
    
