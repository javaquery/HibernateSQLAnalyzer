/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core.parser;

import com.sqlanalyzer.hibernate.core.CriteriaHolder;

/**
 * Parse Criteria and return valued query.
 * @author vicky.thakor
 * @since v2.2
 */
public interface CriteriaParser {
    public void parse(CriteriaHolder criteriaHolder);
}
