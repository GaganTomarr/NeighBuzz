export const validateRegistrationDate=(registrationDeadline: Date| null, eventDate:Date| null)=>{
    if(registrationDeadline==null ) return true;
   else if (eventDate==null) {return false;}
    else if(eventDate>=registrationDeadline){
        return true;
    }
    return false;
}

// export const validateEndTime=(startTime:string, endTime:string)=>{
//     if(endTime==null) return true;
//    DateTime.fromformat(startTime, "HH:mm:ss");
//     else if(startTime.toISOTime())

//       const start=  parseTimeString(startTime);

// }

