package com.tutorial.nano.popularmovies.data;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;

@Entity
public class Movie {
    @Id(autoincrement = false)
    private Long id;

    @NotNull
    private String title;
    @NotNull
    private String posterUrl;
    @NotNull
    private String plot;
    @NotNull
    private Date releaseDate;
    @NotNull
    private Double voteAverage;
    @NotNull
    private String sortPreferences;

    @ToMany(referencedJoinProperty = "movieId")
    private List<MovieTrailer> trailers;

    @ToMany(referencedJoinProperty = "movieId")
    private List<MovieReview> reviews;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1042217376)
    private transient MovieDao myDao;

    @Generated(hash = 1492505484)
    public Movie(Long id, @NotNull String title, @NotNull String posterUrl,
            @NotNull String plot, @NotNull Date releaseDate,
            @NotNull Double voteAverage, @NotNull String sortPreferences) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.sortPreferences = sortPreferences;
    }

    @Generated(hash = 1263461133)
    public Movie() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getPlot() {
        return this.plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSortPreferences() {
        return this.sortPreferences;
    }

    public void setSortPreferences(String sortPreferences) {
        this.sortPreferences = sortPreferences;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1972866248)
    public List<MovieTrailer> getTrailers() {
        if (trailers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieTrailerDao targetDao = daoSession.getMovieTrailerDao();
            List<MovieTrailer> trailersNew = targetDao._queryMovie_Trailers(id);
            synchronized (this) {
                if (trailers == null) {
                    trailers = trailersNew;
                }
            }
        }
        return trailers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 401475982)
    public synchronized void resetTrailers() {
        trailers = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1786094733)
    public List<MovieReview> getReviews() {
        if (reviews == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieReviewDao targetDao = daoSession.getMovieReviewDao();
            List<MovieReview> reviewsNew = targetDao._queryMovie_Reviews(id);
            synchronized (this) {
                if (reviews == null) {
                    reviews = reviewsNew;
                }
            }
        }
        return reviews;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2133376601)
    public synchronized void resetReviews() {
        reviews = null;
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
    @Generated(hash = 215161401)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieDao() : null;
    }
}
