package br.com.blackhunter.finey.rest.finance.analysis.dto.insights;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Insight {
    private String id;
    private String text;
    private String icon;
    private String actionText;
    private String actionType;
    private ActionParams actionParams;
}
