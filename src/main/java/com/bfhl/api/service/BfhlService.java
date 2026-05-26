package com.bfhl.api.service;

import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;

public interface BfhlService {

    /**
     * Processes the input data array and returns categorised results.
     *
     * @param request the incoming request containing the data array
     * @return BfhlResponse with categorised numbers, alphabets, special chars, sum, and concat string
     */
    BfhlResponse processData(BfhlRequest request);
}
