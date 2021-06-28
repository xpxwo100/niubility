package combookproductcontroller.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {
    @ResponseBody
    @ExceptionHandler(value = HospitalException.class)    //异常处理器，处理HospitalException异常
    public ResponseEntity<?> hanlerException(HttpServletRequest request, HospitalException e){
        ErrorInfo<String> error = new ErrorInfo<>();
        error.setCode(ErrorInfo.ERROR);
        error.setMessage(e.getMessage());
        error.setUrl(request.getRequestURI().toString());
        error.setData("some data");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }
}
