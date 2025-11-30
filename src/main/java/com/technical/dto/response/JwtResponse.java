package com.technical.dto.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 7423066825026211730L;
	private String jwttoken;
}
