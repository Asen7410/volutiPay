package com.tr.service;

import com.tr.dto.LoginDto;
import com.tr.util.CommonResponse;

public interface LoginService {

    CommonResponse login(LoginDto loginDto);

}
