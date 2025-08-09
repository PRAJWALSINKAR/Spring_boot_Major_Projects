package prajwal.in.dto;

import lombok.Data;
import prajwal.in.entity.Kid;

import java.util.ArrayList;
import java.util.List;

@Data
public class KidsForm {
    private List<Kid> kids = new ArrayList<>();
}
