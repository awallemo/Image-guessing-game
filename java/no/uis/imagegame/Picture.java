package no.uis.imagegame;

public class Picture {
    private String url;
    private Integer status; // 0 = nothing, 1 = sent
    private Integer id;

    public Picture(String url, Integer status, Integer id){
        this.url = url;
        this.status = status;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
