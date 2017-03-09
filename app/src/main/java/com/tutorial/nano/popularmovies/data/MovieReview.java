package com.tutorial.nano.popularmovies.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class MovieReview {
    @Id(autoincrement = true)
    private Long id;

    @Index
    private Long movieId;

    @ToOne(joinProperty = "movieId")
    private Movie movie;

    @NotNull
    private String author;
    @NotNull
    private String content;
    @NotNull
    private String reviewId;
    @NotNull
    private String ReviewURL;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 244013054)
    private transient MovieReviewDao myDao;
    @Generated(hash = 266336059)
    public MovieReview(Long id, Long movieId, @NotNull String author,
            @NotNull String content, @NotNull String reviewId,
            @NotNull String ReviewURL) {
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.reviewId = reviewId;
        this.ReviewURL = ReviewURL;
    }
    @Generated(hash = 135668893)
    public MovieReview() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMovieId() {
        return this.movieId;
    }
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getReviewId() {
        return this.reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public String getReviewURL() {
        return this.ReviewURL;
    }
    public void setReviewURL(String ReviewURL) {
        this.ReviewURL = ReviewURL;
    }
    @Generated(hash = 708760245)
    private transient Long movie__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 830241488)
    public Movie getMovie() {
        Long __key = this.movieId;
        if (movie__resolvedKey == null || !movie__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieDao targetDao = daoSession.getMovieDao();
            Movie movieNew = targetDao.load(__key);
            synchronized (this) {
                movie = movieNew;
                movie__resolvedKey = __key;
            }
        }
        return movie;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 323362374)
    public void setMovie(Movie movie) {
        synchronized (this) {
            this.movie = movie;
            movieId = movie == null ? null : movie.getId();
            movie__resolvedKey = movieId;
        }
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1705800583)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieReviewDao() : null;
    }
}
