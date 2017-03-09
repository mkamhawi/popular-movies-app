package com.tutorial.nano.popularmovies.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class FavoriteMovie {
    @Id(autoincrement = true)
    private Long id;

    @Index
    private Long movieId;

    @ToOne(joinProperty = "movieId")
    private Movie movie;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1808977242)
    private transient FavoriteMovieDao myDao;

    @Generated(hash = 2138943146)
    public FavoriteMovie(Long id, Long movieId) {
        this.id = id;
        this.movieId = movieId;
    }

    @Generated(hash = 313021928)
    public FavoriteMovie() {
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
    @Generated(hash = 904434069)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavoriteMovieDao() : null;
    }
}
