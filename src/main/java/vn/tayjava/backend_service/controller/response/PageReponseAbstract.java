package vn.tayjava.backend_service.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PageReponseAbstract {
    public int pageNumber;
    public int pageSize;
    public long totalElements;
    public long totalPages;
}
